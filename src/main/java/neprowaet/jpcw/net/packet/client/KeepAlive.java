package neprowaet.jpcw.net.packet.client;

import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Opcode;
import neprowaet.jpcw.net.packet.types.ClientPacket;
import neprowaet.jpcw.net.packet.types.ServerPacket;

/*
	<protocol debug="0" name="KeepAlive" maxsize="16" prior="0" type="90">
		<variable name="code" type="char"/>
	</protocol>
 */

@Opcode(0x5A)
public class KeepAlive extends Packet implements ClientPacket, ServerPacket {
    public byte code;
}
