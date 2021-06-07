package neprowaet.jpcw.net.packet.server;

import neprowaet.jpcw.data.gametypes.PShopItemEntry;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Array;
import neprowaet.jpcw.net.packet.types.ServerPacket;

public class PShopListItem_Re extends Packet implements ServerPacket {

    public long localsid;

    @Array
    public PShopItemEntry[] entries;

    public byte listtype;

    public long page_num;

}
