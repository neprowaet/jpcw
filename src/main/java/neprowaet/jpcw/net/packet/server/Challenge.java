package neprowaet.jpcw.net.packet.server;

import neprowaet.jpcw.data.ConnectionData;
import neprowaet.jpcw.io.annotations.Array;
import neprowaet.jpcw.io.annotations.Opcode;
import neprowaet.jpcw.io.annotations.Skip;
import neprowaet.jpcw.data.Handler;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.net.packet.types.ServerPacket;

import java.util.Arrays;

@Opcode(0x01)
public class Challenge extends Packet implements Handler<ConnectionData>, ServerPacket {
    @Array
    public byte[] nonce;

    @Array("4")
    public byte[] version;

    public byte algo;

    @Array
    public byte[] edition;

    @Skip(1)
    public long exp_rate;

    @Override
    public void handleData(ConnectionData connectionData) {
        connectionData.serverkey = nonce;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "nonce=" + Arrays.toString(nonce) +
                ", version=" + Arrays.toString(version) +
                ", algo=" + algo +
                ", editon=" + Arrays.toString(edition) +
                ", exp_rate=" + exp_rate +
                '}';
    }
}
    /*
    <protocol debug="0" name="Challenge" maxsize="64" prior="101" type="1">
		<variable name="nonce" type="Octets" attr="ref" />
		<variable name="version" type="unsigned int"/>
		<variable name="algo" type="char" defalut="0"/>
		<variable name="edition" type="Octets" attr="ref" />
		<variable name="exp_rate" type="unsigned char" defalut="0"/>
	</protocol>
    */
