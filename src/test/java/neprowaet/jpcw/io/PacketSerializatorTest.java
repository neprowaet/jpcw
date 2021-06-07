package neprowaet.jpcw.io;

import neprowaet.jpcw.net.packet.client.Response;
import neprowaet.jpcw.net.packet.server.Challenge;
import neprowaet.jpcw.net.packet.server.KeyExchange;
import neprowaet.jpcw.net.packet.server.WorldChat;
import neprowaet.jpcw.net.security.HMACMD5HASH;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PacketSerializatorTest {

    @Test
    void desializationSerializationTest() throws Exception {
        Challenge ch = new Challenge();
        ch.nonce = new byte[] {12, 21, 21, 123, 22, 12};
        ch.version = new byte[] {0, 1, 5, 3};
        ch.algo = 0;
        ch.edition = new byte[] {21, 125, 0, -12, 0};
        ch.exp_rate = 123123;

        BinaryPacketBuffer buf = PacketSerializator.serialize(ch, true);
        buf.readCUint();buf.readCUint();
        System.out.println(byteArToHexString(buf.toByteArray()));
        System.out.println(ch);

        Challenge ch_des = PacketSerializator.deserialize(buf, Challenge.class, true);
        assertEquals(ch.toString(), ch_des.toString());
    }

    @Test
    void serialization() throws  Exception {
        //byte[] dump = hexStringToByteArray("10310000d000000000893b563fdeab30e300010502001a333030303030376637393535336636343635353533663634363500cb6b2759");
        byte[] dump = hexStringToByteArray("060c15157b160c000105030005157d00f400000001e0f3");
        BinaryPacketBuffer buf = new BinaryPacketBuffer(dump);

        System.out.println("060c15157b160c000105030005157d00f400000001e0f3 buf size:" + buf.buf.length);

        Challenge ch = PacketSerializator.deserialize(buf, Challenge.class, true);
        System.out.println(byteArToHexString(ch.version) + "<---");
        System.out.println(ch.exp_rate + "<---");

        BinaryPacketBuffer buf2 = PacketSerializator.serialize(ch, true);
        System.out.println(byteArToHexString(buf2.toByteArray()));


        buf2.readCUint();buf2.readCUint();
        System.out.println(buf2.count);
        System.out.println(buf2.pointer);
        Challenge ch2 = PacketSerializator.deserialize(buf2, Challenge.class, true);
        System.out.println(byteArToHexString(ch2.version));
        System.out.println(ch2.exp_rate);

        assertArrayEquals(ch.edition, ch2.edition);
        assertEquals(ch.exp_rate, ch2.exp_rate);
        assertArrayEquals(ch.nonce, ch2.nonce);
        assertEquals(ch.algo, ch2.algo);
        assertArrayEquals(ch.version, ch2.version);

    }

    @Test
    void responseSerialization() {

        byte[] hash = HMACMD5HASH.getHash("asdounng", "U59cf5b52cd5", new byte[]{ 5, 0, 5, 1});
        Response auth = new Response("asdounng", hash, (byte) 0, new byte[] { 5, 0, 5, 1 });
        BinaryPacketBuffer st = PacketSerializator.serialize(auth, true);
        System.out.println(auth);
        System.out.println(byteArToHexString(st.toByteArray()));
    }

    @Test
    void keyExchangeDeserialization() throws Exception {

        byte[] bytes = hexStringToByteArray("1047ad3db27689b809a28c3606b41cb02400");
        KeyExchange des = PacketSerializator.deserialize(new BinaryPacketBuffer(bytes), KeyExchange.class , true);

        System.out.println(Arrays.toString(des.nonce));
        assertArrayEquals(des.nonce, hexStringToByteArray("47ad3db27689b809a28c3606b41cb024"));
    }

    @Test
    void stringProcessingTest() throws Exception {
        //8085 809c 01 00 00081e50 0c 53006b00720069006c006c00 8086 1f04200418042104220420002000210420001f0415042704100422042c042e04200020001204200031003000300020001f0415042904150420042b0420001f041e04200015041604150400e03c0030003e003c0030003a00340033003e0001e03c0030003e003c0030003a00340033003e0002e03c0030003e003c0030003a00340033003e0000
        byte[] bytes = hexStringToByteArray("010000081e500c53006b00720069006c006c0080861f04200418042104220420002000210420001f0415042704100422042c042e04200020001204200031003000300020001f0415042904150420042b0420001f041e04200015041604150400e03c0030003e003c0030003a00340033003e0001e03c0030003e003c0030003a00340033003e0002e03c0030003e003c0030003a00340033003e0000");
        WorldChat msg = PacketSerializator.deserialize(new BinaryPacketBuffer(bytes), WorldChat.class, true);
        System.out.println(msg.name + ": " + msg.msg);
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArToHexString(byte[] ar) {
        StringBuilder sb = new StringBuilder();
        for (byte b : ar)
            sb.append(String.format("%02x", b & 0xff) );
        return sb.toString();
    }
}