package neprowaet.jpcw.io;


//import neprowaet.jpcw.io.annotations.Array;

import neprowaet.jpcw.data.AuthorizationData;
import neprowaet.jpcw.data.ConnectionInfo;
import neprowaet.jpcw.data.Data;
import neprowaet.jpcw.io.annotations.Array;
import neprowaet.jpcw.io.annotations.Skip;
import neprowaet.jpcw.io.annotations.UnsignedInt;
import neprowaet.jpcw.net.packet.server.Challenge;
import neprowaet.jpcw.net.packet.server.OnlineAnnounce;
import org.checkerframework.checker.units.qual.A;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;

public class PacketSerializator {

    public static <T> T deserialize(BinaryPacketStream buf, Class<T> type) throws Exception {
        return deserialize(buf, type, false);
    }

    public static <T> T deserialize(BinaryPacketStream buf, Class<T> type, boolean swap) throws Exception {

        T t = type.getConstructor().newInstance();

        for (Field field : type.getFields()) {
            readField(buf, field, swap, t);
        }

        return t;
    }

    static <T> void readField(BinaryPacketStream buf, Field field, boolean swap, T ret) throws Exception {

        Class<?> fieldType = field.getType();
        field.setAccessible(true);

        if (field.isAnnotationPresent(Skip.class))
            buf.skip(field.getAnnotation(Skip.class).value());

        /*
        GAMETYPES PROCESSING
        */

        if (fieldType.isArray()) {
            Array arrayAnno = field.getAnnotation(Array.class);

            long length;
            if (arrayAnno.value().equals("")) {
                length = buf.readCUint();
            } else {
                try {
                    length = Integer.parseInt(arrayAnno.value());
                } catch (NumberFormatException e) {
                    length = ret.getClass().getField(arrayAnno.value()).getLong(ret);
                }
            }

            if (fieldType.getComponentType().getName().equals("byte")) {
                byte[] ar = buf.readBytes(length, swap);
                field.set(ret, ar);
                return;
            } else {
                /*
                ARRAY GAMETYPES PROCESSING
                */
                return;
            }

        }

        switch (fieldType.getName()) {
            case "short" -> field.setShort(ret, (short) buf.readUByte());
            case "int" -> field.setInt(ret, buf.readInt(swap));
            case "long" -> field.setLong(ret, buf.readUInt(swap));
            case "byte" -> field.setByte(ret, buf.readByte());
        }
    }



    void writeField() {
    }


    public static void main(String[] args) throws Exception {
        byte[] ar = hexStringToByteArray("10310000d000000000893b563fdeab30e300010502001a333030303030376637393535336636343635353533663634363500cb6b2759");
        Challenge ch = PacketSerializator.deserialize(new BinaryPacketStream(ar), Challenge.class, true);

        System.out.println(bytesToHex(ch.nonce));


        byte[] ar2 = hexStringToByteArray("000DB26000027243000000000100000000FFFFFFFF0000000000000000");
        OnlineAnnounce oa = PacketSerializator.deserialize(new BinaryPacketStream(ar2), OnlineAnnounce.class, true);
        System.out.println("_____________________________");
        System.out.println(oa.userid);

        Data data = new Data();
        data.AuthorizationData = new AuthorizationData();
        data.ConnectionInfo = new ConnectionInfo();
        //ch.handle(data);

        System.out.println("_____________________________");
        byte[] ar3 = hexStringToByteArray("0380a3077075676f766b6110e6976aa8415dc03559f28ac3951bba0e008087042e222e755956720c2c27630625246805682364072726680668500670552660002522660757236a0426551c0e276e62042e566a0e22236876522814752e20110e206e0051757e26517f3202775d77727356577272757f3b586d32115b7a66205b787e37462e272e70514119605b427f032241076745216802687c3744667d2555716668036812 7272757f3b586d32115b7a66205b787e37462e272e70514119605b427f032241076745216802687c3744667d2555716668036812 7272757f3b586d32115b7a66205b787e37462e272e70514119605b427f032241076745216802687c3744667d2555716668036812");
        BinaryPacketStream stream = new BinaryPacketStream(ar3);
        System.out.println(stream.readCUint());
        System.out.println(stream.readCUint());
    }


    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
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

}

