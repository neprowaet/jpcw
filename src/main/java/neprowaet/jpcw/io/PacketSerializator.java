package neprowaet.jpcw.io;

import neprowaet.jpcw.io.annotations.*;
import java.lang.reflect.Field;
import java.nio.BufferUnderflowException;

public class PacketSerializator {


    private PacketSerializator() {
    }

    public static <T> T deserialize(BinaryPacketBuffer buf, Class<T> type) throws Exception {
        return deserialize(buf, type, false);
    }

    public static <T> T deserialize(BinaryPacketBuffer buf, Class<T> type, boolean swap) throws Exception {

        T t = type.getConstructor().newInstance();
        try {
            for (Field field : type.getFields()) {
                readField(buf, field, swap, t);
            }
        } catch (BufferUnderflowException e) {
            e.printStackTrace();
        }

        return t;
    }

    private static <T> void readField(BinaryPacketBuffer buf, Field field, boolean swap, T ret) throws Exception {

        Class<?> fieldType = field.getType();
        field.setAccessible(true);

        if (field.isAnnotationPresent(Skip.class))
            buf.skip(field.getAnnotation(Skip.class).value());

        if (field.isAnnotationPresent(If.class))
            if (!ret.getClass().getField(field.getAnnotation(If.class).value()).getBoolean(ret)) return;

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

        switch (fieldType.getSimpleName()) {
            case "short" -> field.setShort(ret, (short) buf.readUByte());
            case "int" -> field.setInt(ret, buf.readInt(swap));
            case "long" -> field.setLong(ret, buf.readUInt(swap));
            case "byte" -> field.setByte(ret, buf.readByte());
            case "boolean" -> field.setBoolean(ret, buf.readByte() != 0);
            case "String" -> field.set(ret, buf.readUString(swap));
        }
    }


    public static <T extends Packet> BinaryPacketBuffer serialize(T packet, boolean swap) {
        int value = packet.getClass().getAnnotation(Opcode.class).value();

        Field[] fields = packet.getClass().getDeclaredFields();
        BinaryPacketBuffer buf = new BinaryPacketBuffer();
        if (swap)
            buf.setBigEndian();
        if (!swap)
            buf.setLittleEndian();


        try {
            for (Field f : fields) {
                writeField(f, buf, packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return new BinaryPacketBuffer().writeCUInt(value).writeCUInt(buf.count).writeBytes(buf.toByteArray());
    }

    private static void writeField(Field field, BinaryPacketBuffer buf, Packet packet) throws IllegalAccessException {

        Class<?> fieldType = field.getType();

        if (field.isAnnotationPresent(Ignore.class))
            return;

        if (field.isAnnotationPresent(Skip.class))
            for (int i = 0; i < field.getAnnotation(Skip.class).value(); i++)
                buf.writeByte((byte) 0);


        if (field.isAnnotationPresent(Array.class)) {
            Array annotation = field.getAnnotation(Array.class);


            try {
                Integer.parseInt(annotation.value());
            } catch (NumberFormatException e) {
                int length = java.lang.reflect.Array.getLength(field.get(packet));
                buf.writeCUInt(length);
            }

            if (fieldType.getComponentType().getName().equals("byte")) {
                byte[] ar = (byte[]) field.get(packet);
                buf.writeBytes(ar);
                return;
            } else {
                /*
                ARRAY GAMETYPES PROCESSING
                 */
            }
        }

        if (field.isAnnotationPresent(Swap.class))
            buf.swap();

        switch (fieldType.getSimpleName()) {
            case "short" -> buf.writeUShort((int) field.get(packet));
            case "int" -> buf.writeUInt((int) field.get(packet));
            case "long" -> buf.writeUInt((long) field.get(packet));
            case "byte" -> buf.writeByte((byte) field.get(packet));
            case "boolean" -> {
                if ((boolean) field.get(packet)) {
                    buf.writeByte((byte) 1);
                } else {
                    buf.writeByte((byte) 0);
                }
            }
            case "String" -> buf.writeUString((String) field.get(packet));
            default -> System.out.println("WAI");
        }

        if (field.isAnnotationPresent(Swap.class))
            buf.swap();

    }
}
