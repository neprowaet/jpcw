package neprowaet.jpcw.data.gametypes;

import neprowaet.jpcw.io.BinaryPacketBuffer;

public class PShopItemEntry implements SerializableType{
    public long roleid;
    public PShopItem pShopItem;

    @Override
    public void deserialize(BinaryPacketBuffer stream) {
        stream.swap();
        this.roleid = stream.readUInt();
        this.pShopItem = new PShopItem();
        this.pShopItem.deserialize(stream);
        stream.swap();
    }
}
