package neprowaet.jpcw.net;

import com.google.common.reflect.TypeToken;
import neprowaet.jpcw.data.AuthorizationData;
import neprowaet.jpcw.data.ConnectionInfo;
import neprowaet.jpcw.data.Data;
import neprowaet.jpcw.io.BinaryPacketStream;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.PacketRegistry;
import neprowaet.jpcw.io.PacketSerializator;
import neprowaet.jpcw.net.packet.server.Challenge;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;

public class Client {

    public Data data;
    private Connection connection;

    private HashMap<Class<? extends Packet>, PacketListener<? extends Packet>> listeners = new HashMap<>();
    private boolean connected;

    protected Client(Connection connection) {
        this.connection = connection;

        this.data = new Data();
        this.data.ConnectionInfo = new ConnectionInfo();
        this.data.AuthorizationData = new AuthorizationData();
    }

    private Client() {}

    public void read(SocketChannel socket) throws Exception {

        ByteBuffer bb = ByteBuffer.allocate(1024);
        int readedLenght = socket.read(bb);
        BinaryPacketStream stream = new BinaryPacketStream(bb.array(), readedLenght);
        long opcode = stream.readCUint();
        long packetLength = stream.readCUint();
        PacketRegistry.get((int) opcode);
        Packet p = (Packet) PacketSerializator.deserialize(stream, PacketRegistry.get((int) opcode), true);


        PacketListener packetListener = listeners.get(PacketRegistry.get((int) opcode));
        packetListener.onPacket(p);
    }




    public <T extends Packet> void addListener(Class<T> packetType, PacketListener<T> packetListener) {
        listeners.put(packetType, packetListener);
    }
}
