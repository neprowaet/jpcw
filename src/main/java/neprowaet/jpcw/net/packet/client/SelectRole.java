package neprowaet.jpcw.net.packet.client;

import neprowaet.jpcw.data.AuthorizationData;
import neprowaet.jpcw.data.Handler;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Ignore;
import neprowaet.jpcw.io.annotations.Opcode;
import neprowaet.jpcw.net.packet.types.ClientPacket;

@Opcode(0x46)
public class SelectRole extends Packet implements Handler<AuthorizationData>, ClientPacket {
    public long roleid;
    public byte flag;

    @Ignore
    public int roleSlot;

    public SelectRole() {}

    public SelectRole(int roleSlot) {
        this.roleSlot = roleSlot;
    }

    @Override
    public void handleData(AuthorizationData data) { /* <-- TODO: replace with RoleInfoData */
        this.flag = (byte)0;
        this.roleid = data.roles.get(roleSlot).roleid;
        data.roleSlot = this.roleSlot;
        data.selectedRoleid = this.roleid;
    }
}

/* 46 05 000C6090 00
	<protocol debug="0" name="SelectRole" maxsize="32" prior="101" type="70">
		<variable name="roleid" type="int"/>
		<variable name="flag" type="char" default="0"/>
	</protocol>
 */
