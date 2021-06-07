package neprowaet.jpcw.data.gametypes;

import neprowaet.jpcw.io.BinaryPacketBuffer;

public interface SerializableType {
    void deserialize(BinaryPacketBuffer stream);

}
