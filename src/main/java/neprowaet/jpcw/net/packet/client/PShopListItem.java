package neprowaet.jpcw.net.packet.client;

import neprowaet.jpcw.data.AuthorizationData;
import neprowaet.jpcw.data.ConnectionData;
import neprowaet.jpcw.data.Handler;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Opcode;
import neprowaet.jpcw.net.packet.types.ClientPacket;


@Opcode(0x3A0)
public class PShopListItem extends Packet implements Handler<AuthorizationData>, ClientPacket {

    public long roleid;

    public long localsid;

    public long itemid;

    public byte listtype;

    public long page_num;

    public PShopListItem(long itemid, long page_num) {
        this.itemid = itemid;
        this.page_num = page_num;
    }

    @Override
    public void handleData(AuthorizationData data) {
        this.roleid = data.selectedRoleid;
        this.localsid = data.localsid;
        this.listtype = (byte)0;
    }
}
