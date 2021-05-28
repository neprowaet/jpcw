package neprowaet.jpcw.data.gametypes;

import neprowaet.jpcw.io.BinaryPacketStream;

public interface SerializableType {
    public void deserialize(BinaryPacketStream stream);
}
