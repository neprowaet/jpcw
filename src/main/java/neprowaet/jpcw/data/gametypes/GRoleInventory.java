package neprowaet.jpcw.data.gametypes;

import neprowaet.jpcw.io.BinaryPacketBuffer;

public class GRoleInventory implements SerializableType {
    public long id;
    public long pos;
    public long count;
    public long max_count;
    public byte[] data;
    public long proctype;
    public long expiredata;
    public long guid1;
    public long guid2;
    public long mask;


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
