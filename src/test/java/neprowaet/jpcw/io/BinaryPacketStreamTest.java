package neprowaet.jpcw.io;


import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BinaryPacketStreamTest {

    BinaryPacketStream buf;

    @Test
    public void writeTest() {
        //ci ci ci          byte[0x10]               byte[4] b  ci              byte[0x1a]                              b    uint
        //01 36 10 310000d000000000893b563fdeab30e3 00010502 00 1a 3330303030303766373935353366363436353535336636343635 00 cb6b2759
        String dump = "013610310000d000000000893b563fdeab30e300010502001a333030303030376637393535336636343635353533663634363500cb6b2759";

        buf = new BinaryPacketStream();
        buf.setBigEndian();
        buf.writeCUInt(1);
        buf.writeCUInt(54);
        buf.writeCUInt(16);
        buf.writeBytes(hexStringToByteArray("310000d000000000893b563fdeab30e3"));
        buf.writeBytes(hexStringToByteArray("00010502"));
        buf.writeByte((byte) 0x00);
        buf.writeCUInt(26);
        buf.writeBytes(hexStringToByteArray("3330303030303766373935353366363436353535336636343635"));
        buf.writeByte((byte) 0x00);
        buf.writeUInt(3412797273L);

        String result = byteArToHexString(buf.toByteArray());
        System.out.println(dump);
        System.out.println(result);

        assertEquals(result, dump);
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