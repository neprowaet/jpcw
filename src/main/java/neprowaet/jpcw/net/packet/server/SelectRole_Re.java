package neprowaet.jpcw.net.packet.server;

import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Array;
import neprowaet.jpcw.net.packet.types.ServerPacket;


public class SelectRole_Re extends Packet implements ServerPacket {
    public long result;
    @Array
    public byte[] auth;

}
/* 47 05 00000000 00
	<protocol debug="0" name="SelectRole_Re" maxsize="1024" prior="101" type="71">
		<variable name="result" type="int"/>
		<variable name="auth" type="ByteVector" default="ByteVector()"/>
	</protocol>
 */