package neprowaet.jpcw.io;

import neprowaet.jpcw.net.packet.client.KeepAlive;
import neprowaet.jpcw.net.packet.client.PShopListItem;
import neprowaet.jpcw.net.packet.client.SelectRole;
import neprowaet.jpcw.net.packet.server.*;
import neprowaet.jpcw.net.packet.types.ServerPacket;

import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {
    static Map<Integer, Class<?>> serverPackets = new HashMap<>();
    static Map<Integer, Class<?>> serverContainerPackets = new HashMap<>();

    static { /* TODO: autoregister packets from classpath */
        register(0x01, new Challenge());
        register(0x02, new KeyExchange());
        register(0x04, new OnlineAnnounce());
        register(0x53, new RoleList_Re());
        register(0x47, new SelectRole_Re());
        register(0x85, new WorldChat());
        register(0x3A1, new PShopListItem_Re());

        register(0x5A, new KeepAlive());
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
