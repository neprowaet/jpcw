package neprowaet.jpcw.net;

import neprowaet.jpcw.data.AuthorizationData;
import neprowaet.jpcw.data.ConnectionData;
import neprowaet.jpcw.data.Data;
import neprowaet.jpcw.data.Handler;
import neprowaet.jpcw.io.BinaryPacketBuffer;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.PacketRegistry;
import neprowaet.jpcw.io.PacketSerializator;
import neprowaet.jpcw.net.packet.client.*;
import neprowaet.jpcw.net.packet.server.*;
import neprowaet.jpcw.net.security.MPPC;
import neprowaet.jpcw.net.security.Security;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class Client {

    public Data data;
    private Connection connection;
    protected SocketChannel channel;

    public final HashMap<Class<? extends Packet>, List<PacketListener<? extends Packet>>> listeners = new HashMap<>();
    private final ConcurrentHashMap<Class<? extends Packet>, ConcurrentLinkedQueue<FuturePacket<? extends Packet>>> futurepackets = new ConcurrentHashMap<>();
    private boolean connected;
    private volatile boolean authorized;

    ByteBuffer recvb = ByteBuffer.allocate(2 * 24);
    BinaryPacketBuffer databuf = new BinaryPacketBuffer();

    private Client() {
    }

    protected Client(Connection connection) {
        this.connection = connection;

        this.data = new Data();
        this.data.ConnectionData = new ConnectionData();
        this.data.AuthorizationData = new AuthorizationData();
    }

    public void connect() {
        try {
            channel.connect(connection.address);
            connected = true;
        } catch (IOException e) {
            System.out.println("can't connect");
        }
    }

    public void disconnect() {
        try {
            channel.close();
            connected = false;
            System.out.println(data.ConnectionData.username + " disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Security> encodingChain = new ArrayList<>();
    private List<Security> decodingChain = new ArrayList<>();

    public void addEncoder(Security s) {
        encodingChain.add(s);
    }

    public void addDecoder(Security s) {
        decodingChain.add(s);
    }

    public void read(SocketChannel socket) throws Exception {
        int readedLength = socket.read(recvb);
        if (readedLength == -1) { socket.close();
            System.out.println("closed connection"); return; }
        if (readedLength == 0) return;

        recvb.flip();
        byte[] temp = new byte[readedLength];
        recvb.get(temp);
        recvb.compact();

        if (data.ConnectionData.encryption)
            for (Security s : decodingChain)
                temp = s.unpack(temp);

        databuf.writeBytes(temp);
        //System.out.println("[S->C] len: " + readedLength + " data: " + byteArToHexString(databuf.toByteArray()));
        while (true) {
            int opcode, packetlen;
            if (!databuf.tryReadCUint()) { databuf.reset(); return; } // buffer doesn't filled enought to read opcode
            opcode = (int) databuf.readCUint();

            if (!databuf.tryReadCUint()) { databuf.reset(); return; } // buffer doesn't filled enought to read next packet length
            packetlen = (int) databuf.readCUint();
            int headerlen = databuf.pointer;

            if (databuf.remaining() < packetlen) { databuf.reset(); return; } // not enought data to read full packet

            if (!PacketRegistry.contains(opcode)) { databuf.skip(packetlen); databuf.compact(); return; } // skip data if packet unregistered


            Packet p = (Packet) PacketSerializator.deserialize(databuf, PacketRegistry.get(opcode), true);

            databuf.position(headerlen + packetlen);
            databuf.compact();


            if (p instanceof Handler handler)
                handler.handle(this.data);

            notifyListeners(opcode, p);
            notifyFuturePackets(opcode, p);
        }
    }

    private void notifyListeners(int opcode, Packet p) {
        Class packetType = PacketRegistry.get(opcode);
        if(listeners.get(packetType) != null) {
            for (PacketListener packetListener : listeners.get(packetType)) {
                packetListener.onPacket(p);
            }
        }
    }

    private void notifyFuturePackets(int opcode, Packet p) {
        Class packetType = PacketRegistry.get(opcode);
        if(futurepackets.containsKey(packetType)) {
            FuturePacket<? extends Packet> futurepacket = futurepackets.get(packetType).poll();
            if(futurepacket != null) {
                futurepacket.set(p);
            }
        }
    }

    public void enableAutoAuth() {
        addListener(Challenge.class, p ->  write(new Response(), true));

        addListener(KeyExchange.class, p -> write(new KeyExchangeC2S(), true));

        addListener(OnlineAnnounce.class, p -> write(new RoleList(-1), true));

        addListener(RoleList_Re.class, p -> {
            if(p.roleProvided) {
                write(new RoleList(p.handle), true);
            } else {
                write(new SelectRole(0), true);
            }
        });

        addListener(SelectRole_Re.class, p -> {
            write(new EnterWorld() ,true);
            authorized = true;
            System.out.println(data.ConnectionData.username + " entered world");});
    }

    public void write(Packet p) {
        write(p, false);
    }

    public void write(Packet p, boolean force) {

        while(!force && !authorized)
            Thread.onSpinWait();

        if (p instanceof Handler handler) handler.handle(this.data);

        BinaryPacketBuffer buffer = PacketSerializator.serialize(p, true);
        //System.out.println("[C->S] len: " + data.size() + " data: " + byteArToHexString(data.toByteArray()));

        byte[] temp = buffer.toByteArray();

        if (this.data.ConnectionData.encryption)
            for (Security s : encodingChain)
                temp = s.unpack(temp);

        ByteBuffer b = ByteBuffer.wrap(temp);
        try {
            while (b.hasRemaining())
                channel.write(b);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends Packet> FuturePacket<T> sendAndWaitFor (Packet p, Class<T> towait) {
        FuturePacket<T> toreturn = new FuturePacket<>();

        if (!futurepackets.containsKey(towait)) {
            futurepackets.put(towait, new ConcurrentLinkedQueue<>());
        }

        futurepackets.get(towait).add(toreturn);
        write(p);

        return toreturn;
    }

    public void writeRaw(byte[] bytes) {
        ByteBuffer b = ByteBuffer.wrap(bytes);
        try {
            while (b.hasRemaining())
                channel.write(b);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T extends Packet> void addListener(Class<T> packetType, PacketListener<T> packetListener) {
        if (!listeners.containsKey(packetType))
            listeners.put(packetType, new CopyOnWriteArrayList<>());
        listeners.get(packetType).add(packetListener);
    }
}
