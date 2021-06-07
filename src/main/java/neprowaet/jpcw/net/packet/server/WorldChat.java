package neprowaet.jpcw.net.packet.server;

import neprowaet.jpcw.io.Packet;
import neprowaet.jpcw.io.annotations.Opcode;
import neprowaet.jpcw.net.packet.types.ServerPacket;

@Opcode(0x85)
public class WorldChat extends Packet implements ServerPacket {

    public byte channel;
    public byte emotion;
    public long roleid;
    public String name;
    public String msg;

    @Override
    public String toString() {
        return "WorldChat{" +
                "channel=" + channel +
                ", emotion=" + emotion +
                ", roleid=" + roleid +
                ", name='" + name + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}

/*
	<protocol debug="0" name="WorldChat" maxsize="1024" prior="1" type="133"> <!-- link server to player -->
		<variable name="channel" type="unsigned char"/>
		<variable name="emotion" type="unsigned char"/>
		<variable name="roleid" type="int"/>
		<variable name="name" type="Octets" attr="ref"/>
		<variable name="msg" type="Octets" attr="ref"/>
		<variable name="data" type="Octets" attr="ref"/>
	</protocol>

	[S->C] len: 107 data:
	8085 809c 01 00 00081e50 0c 53006b00720069006c006c00 8086 1f04200418042104220420002000210420001f0415042704100422042c042e04200020001204200031003000300020001f0415042904150420042b0420001f041e04200015041604150400e03c0030003e003c0030003a00340033003e0001e03c0030003e003c0030003a00340033003e0002e03c0030003e003c0030003a00340033003e0000
16777224
 */
