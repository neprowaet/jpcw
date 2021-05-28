package neprowaet.jpcw.data.gametypes;

import neprowaet.jpcw.io.BinaryPacketStream;
import neprowaet.jpcw.io.Packet;

public interface SerializableType {
    void deserialize(BinaryPacketStream stream);

}
