package neprowaet.jpcw.net.packet.client;

import neprowaet.jpcw.data.ConnectionData;
import neprowaet.jpcw.data.Handler;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Array;
import neprowaet.jpcw.io.annotations.Opcode;
import neprowaet.jpcw.net.packet.types.ClientPacket;
import neprowaet.jpcw.net.security.HMACMD5HASH;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
@Opcode(0x03)
public class Response extends Packet implements Handler<ConnectionData>, ClientPacket {

    public Response() {
    }

    @Override
    public String toString() {
        return "Response{" +
                "identity=" + Arrays.toString(identity) +
                ", response=" + Arrays.toString(response) +
                ", use_token=" + use_token +
                ", cli_fingerprint=" + Arrays.toString(cli_fingerprint) +
                '}';
    }

    public Response(String username, byte[] response, byte use_token, byte[] cli_fingerprint) {
        this.identity = username.getBytes(StandardCharsets.US_ASCII);
        this.response = response;
        this.use_token = use_token;
        this.cli_fingerprint = cli_fingerprint;
    }

    @Array
    public byte[] identity;
    @Array
    public byte[] response;

    public byte use_token;

    @Array
    public byte[] cli_fingerprint;

    @Override
    public void handleData(ConnectionData data) {
        this.identity = data.username.getBytes(StandardCharsets.US_ASCII);
        this.response = HMACMD5HASH.getHash(data.username, data.password, data.serverkey);
        this.use_token = (byte) 0;
        this.cli_fingerprint = new byte[]{5, 0, 5, 1};
    }
}
