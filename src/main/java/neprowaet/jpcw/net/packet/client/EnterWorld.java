package neprowaet.jpcw.net.packet.client;

import neprowaet.jpcw.data.AuthorizationData;
import neprowaet.jpcw.data.Handler;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Array;
import neprowaet.jpcw.io.annotations.Opcode;
import neprowaet.jpcw.net.packet.types.ClientPacket;

@Opcode(0x48)
public class EnterWorld extends Packet implements Handler<AuthorizationData>,ClientPacket {

    public long roleid;

    @Array("0")
    public byte[] unknown = new byte[20];

    @Override
    public void handleData(AuthorizationData data) {
        this.roleid = data.selectedRoleid;
    }
}
