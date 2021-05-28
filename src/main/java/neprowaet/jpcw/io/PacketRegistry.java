package neprowaet.jpcw.io;

import neprowaet.jpcw.net.packet.server.Challenge;
import neprowaet.jpcw.net.packet.types.ServerPacket;

import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {
    static Map<Integer, Class<?>> serverPackets = new HashMap<>();
    static Map<Integer, Class<?>> serverContainerPackets = new HashMap<>();

    static {
        register(1, new Challenge());
    }

    public static <T extends Packet> void register(int opcode, T packet) {
        if (packet == null) return;

        if (packet instanceof ServerPacket)
            serverPackets.put(opcode, packet.getClass());
    }

    public static Class<?> get(int opcode) {
        return serverPackets.get(opcode);
    }

    public static boolean contains(int opcode) {
        return serverPackets.containsKey(opcode);
    }

    public static void main(String[] args) {

    }
}
