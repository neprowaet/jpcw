package neprowaet.jpcw.net.packet.client;

import neprowaet.jpcw.data.AuthorizationData;
import neprowaet.jpcw.data.Handler;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Opcode;
import neprowaet.jpcw.io.annotations.Swap;
import neprowaet.jpcw.net.packet.types.ClientPacket;

@Opcode(0x52)
public class RoleList extends Packet implements Handler<AuthorizationData>, ClientPacket {

    public long userid;
    public long localid;
    @Swap
    public long handle;


    public RoleList() {
    }

    public RoleList(long userid, long localid, long handle) {
        this.userid = userid;
        this.localid = localid;
        this.handle = handle;
    }

    public RoleList(long handle) {
        this.handle = handle;
    }

    @Override
    public void handleData(AuthorizationData data) {
        this.userid = data.userid;
        this.localid = 0;
        //if(handle != -1)
        //    this.handle = -1;
    }
}

/* 520C000DB26000000000FFFFFFFF

	<protocol debug="0" name="RoleList" maxsize="32" prior="101" type="82">
		<variable name="userid" type="int"/>
		<variable name="localsid" type="unsigned int"/>
		<variable name="handle" type="int" />
	</protocol>
 */
