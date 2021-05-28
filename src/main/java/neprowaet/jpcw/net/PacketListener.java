package neprowaet.jpcw.net;

import neprowaet.jpcw.io.Packet;

@FunctionalInterface
public interface PacketListener<T extends Packet>  {
    void onPacket(T packet);
}
