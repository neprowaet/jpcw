package neprowaet.jpcw.data.gametypes;

import neprowaet.jpcw.io.BinaryPacketBuffer;

public class PShopItem implements SerializableType {
    public GRoleInventory item;
    public long price;
    long reserved;
    long reserved2;


    @Override
    public void deserialize(BinaryPacketBuffer stream) {
        this.item = new GRoleInventory();
        this.item.deserialize(stream);
        this.price = stream.readUInt();
        this.reserved = stream.readUInt();
        this.reserved2 = stream.readUInt();
    }
}
