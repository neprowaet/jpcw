package neprowaet.jpcw.net.packet.client;

import neprowaet.jpcw.data.ConnectionData;
import neprowaet.jpcw.data.Handler;
import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Array;
import neprowaet.jpcw.io.annotations.Opcode;
import neprowaet.jpcw.net.packet.types.ClientPacket;
import neprowaet.jpcw.net.packet.types.ServerPacket;

import java.util.Random;

/*
	<protocol debug="0" name="KeyExchange" maxsize="32" prior="101" type="2">
		<variable name="nonce" type="Octets" attr="ref" />
		<variable name="blkickuser" type="char"	default="0"/>
	</protocol>
 */
@Opcode(0x02)
public class KeyExchangeC2S extends Packet implements Handler<ConnectionData>, ClientPacket {

    public KeyExchangeC2S() {
    }

    @Array
    public byte[] nonce;

    public byte blkickuser;

    @Override
    public void handleData(ConnectionData data) {
        byte[] random = new byte[16];
        new Random().nextBytes(random);

        this.nonce = random;
        this.blkickuser = (byte) 1;

        data.oseckey = random;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if(nonce.length != 0)
        for (byte b : nonce)
            sb.append(String.format("%02x", b & 0xff));

        return "KeyExchange{" +
                "nonce=" + sb +
                ", blkickuser=" + blkickuser +
                '}';
    }
}
