package neprowaet.jpcw.net.packet.client;

import neprowaet.jpcw.data.ConnectionInfo;
import neprowaet.jpcw.data.Data;
import neprowaet.jpcw.data.Handler;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Array;
import neprowaet.jpcw.net.packet.types.ClientPacket;

/*
	<protocol debug="0" name="Response" maxsize="512" prior="101" type="3">
		<variable name="identity" type="Octets" attr="ref" />
		<variable name="response" type="Octets" attr="ref" />
		<variable name="use_token" type="char" defalut="0"/>
		<variable name="cli_fingerprint" type="Octets" attr="ref" />
	</protocol>
	03 80a3 07 7075676f766b61 10 e6976aa8415dc03559f28ac3951bba0e 00
	8087 042e222e755956720c2c27630625246805682364072726680668500670552660002522660757236a0426551c0e276e62042e566a0e22236876522814752e20110e206e0051757e26517f3202775d77727356577272757f3b586d32115b7a66205b787e37462e272e70514119605b427f032241076745216802687c3744667d2555716668036812
 */
public class Response extends Packet implements Handler<ConnectionInfo>, ClientPacket {

    @Array
    byte[] identity;
    @Array
    byte[] response;

    byte use_token;

    @Array
    byte[] cli_fingerprint;

    @Override
    public void handleData(ConnectionInfo dataBlock) {

    }
}
