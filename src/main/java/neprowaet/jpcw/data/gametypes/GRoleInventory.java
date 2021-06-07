package neprowaet.jpcw.data.gametypes;

import neprowaet.jpcw.io.BinaryPacketBuffer;

public class GRoleInventory implements SerializableType {
    long id;
    long pos;
    long count;
    long max_count;
    byte[] data;
    long proctype;
    long expiredata;
    long guid1;
    long guid2;
    long mask;


    @Override
    public void deserialize(BinaryPacketBuffer stream) {
        this.id = stream.readUInt();
        this.pos = stream.readUInt();
        this.count = stream.readUInt();
        this.max_count = stream.readUInt();
        this.data = stream.readBytes(stream.readCUint());
        this.proctype = stream.readUInt();
        this.expiredata = stream.readUInt();
        this.guid1 = stream.readUInt();
        this.guid2 = stream.readUInt();
        this.mask = stream.readUInt();
    }
}
