package neprowaet.jpcw.data.gametypes;

import neprowaet.jpcw.io.BinaryPacketStream;

public class PShopItemEntry implements SerializableType{
    long roleid;
    PShopItem pShopItem;

    @Override
    public void deserialize(BinaryPacketStream stream) {
        this.roleid = stream.readUInt();
        this.pShopItem = new PShopItem();
        this.pShopItem.deserialize(stream);
    }
}
