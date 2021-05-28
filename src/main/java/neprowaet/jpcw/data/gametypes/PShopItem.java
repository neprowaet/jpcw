package neprowaet.jpcw.data.gametypes;

import neprowaet.jpcw.io.BinaryPacketStream;

public class PShopItem implements SerializableType {
    GRoleInventory item;
    long price;
    long reserved;
    long reserved2;


    @Override
    public void deserialize(BinaryPacketStream stream) {
        this.item = new GRoleInventory();
        this.item.deserialize(stream);
        this.price = stream.readUInt();
        this.reserved = stream.readUInt();
        this.reserved2 = stream.readUInt();
    }
}
