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

    ByteBuffer recvb = ByteBuffer.allocate(2 * 1024);
    BinaryPacketBuffer databuf = new BinaryPacketBuffer();

    public <T extends Packet> FuturePacket<T> sendAndWaitFor (Packet p, Class<T> towait) {
        FuturePacket<T> toreturn = new FuturePacket<>();

        if (!futurepackets.containsKey(towait)) {
            futurepackets.put(towait, new ConcurrentLinkedQueue<>());
        }

        futurepackets.get(towait).add(toreturn);
        //write(p);

        return toreturn;
    }

    public <T extends Packet> FuturePacket<T> secondAttempt (Packet p, Class<T> towait) {
        FuturePacket<T> toreturn = new FuturePacket<>();

        addListener(towait, (packet) -> { toreturn.set(packet); listeners.get(towait).remove(listeners.get(towait).size()-1);});
        write(p);

        return toreturn;
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
        } catch (IOException e) {
            System.out.println("can't connect");
        }
    }

    MPPC mppc = new MPPC();

    private Client() {
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void read(SocketChannel socket) throws Exception {
        int readedLength = socket.read(recvb);
        if (readedLength == -1) { socket.close();
            System.out.println("closed connection"); return; }
        if (readedLength == 0) return;

        recvb.flip();
        byte[] temp = new byte[readedLength];
        recvb.get(temp);
        recvb.compact();

        if(data.ConnectionData.encryption)
            temp = mppc.unpack(temp);

        databuf.writeBytes(temp);
        System.out.println("[S->C] len: " + readedLength + " data: " + byteArToHexString(databuf.toByteArray()));
        while (true) {
            int opcode, packetlen;
            if (!databuf.tryReadCUint()) { databuf.reset(); return; } // buffer doesn't filled enought to read opcode
            opcode = (int) databuf.readCUint();

            if (!databuf.tryReadCUint()) { databuf.reset(); return; } // buffer doesn't filled enought to read next packet length
            packetlen = (int) databuf.readCUint();
            int headerlen = databuf.pointer;

            if (databuf.remaining() < packetlen) { databuf.reset(); return; } // not enought data to read full packet

            if (!PacketRegistry.contains(opcode)) { databuf.skip(packetlen); databuf.compact(); return; } // skip data if packet unregistered
            Class packetType = PacketRegistry.get(opcode);


            Packet p = (Packet) PacketSerializator.deserialize(databuf, packetType, true);

            databuf.position(headerlen + packetlen);
            databuf.compact();


            if (p instanceof Handler handler)
                handler.handle(this.data);

            if(listeners.get(packetType) != null)
            for (PacketListener packetListener : listeners.get(packetType))
                packetListener.onPacket(p);

            if(futurepackets.containsKey(packetType)) {
                FuturePacket<? extends Packet> futurepacket = futurepackets.get(packetType).poll();
                if(futurepacket != null) {
                    futurepacket.set(p);
                }

            }

        }
    }

    public void enableAutoAuth() {
        addListener(Challenge.class, p ->  write(new Response()));

        addListener(KeyExchange.class, p -> write(new KeyExchangeC2S()));

        addListener(OnlineAnnounce.class, p -> write(new RoleList(-1)));

        addListener(RoleList_Re.class, p -> {
            if(p.roleProvided) {
                write(new RoleList(p.handle));
            } else {
                write(new SelectRole(0));
            }
        });

        addListener(SelectRole_Re.class, p -> write(new EnterWorld()));
    }

    public void write(Packet p) {
        if (p instanceof Handler handler) handler.handle(this.data);

        BinaryPacketBuffer data = PacketSerializator.serialize(p, true);
        System.out.println("[C->S] len: " + data.size() + " data: " + byteArToHexString(data.toByteArray()));
        ByteBuffer b = ByteBuffer.wrap(data.toByteArray());
        try {
            while (b.hasRemaining())
                channel.write(b);

        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public static String byteArToHexString(byte[] ar) {
        StringBuilder sb = new StringBuilder();
        for (byte b : ar)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
