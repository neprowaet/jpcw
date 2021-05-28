package neprowaet.jpcw.io;

import java.nio.BufferUnderflowException;
import java.util.Arrays;

public class BinaryPacketStream {

    public byte[] buf = new byte[16];

    public int pointer = 0;
    public int count = 0;

    private boolean swap = false;

    public BinaryPacketStream() {
    }

    public BinaryPacketStream(byte[] ar) {
        this.buf = ar;
        this.count = ar.length;
    }

    public BinaryPacketStream(byte[] ar, int len) {
        this.buf = Arrays.copyOf(ar, len);
    }

    public byte[] toByteArray() {
        byte[] ret = new byte[count];
        System.arraycopy(this.buf, 0, ret, 0, count);

        return ret;
    }

    public void setBigEndian() {
        this.swap = false;
    }

    public void setLittleEndian() {
        this.swap = true;
    }

    public BinaryPacketStream writeUInt(long l) {
        writeUInt(l, this.swap);
        return this;
    }

    public BinaryPacketStream writeCUInt(long l) {
        if (l < 0x80)
            return writeByte((byte) l);

        if (l < 0x4000)
            return writeUShort((int) (l | 0x8000), true);

        if (l < 0x20000000)
            return writeUInt(l | 0xC0000000, true);

        writeByte((byte) 0xE0);

        return writeUInt(l, true);
    }

    public BinaryPacketStream writeUShort(int i) {
        return writeUShort(i, this.swap);
    }
    public BinaryPacketStream writeUShort(int i, boolean swap) {
        i = i & 0xFFFF;

        byte[] shortByteAr = new byte[]{
                (byte) (i >>> 8),
                (byte) (i)
        };

        writeBytes(shortByteAr, swap);
        return this;
    }

    public BinaryPacketStream writeUInt(long l, boolean swap) {
        l = l & 0xFFFFFFFFL;
        int i = (int) l;

        byte[] intByteAr = new byte[]{
                (byte) (i >>> 24),
                (byte) (i >>> 16),
                (byte) (i >>> 8),
                (byte) (i)
        };

        writeBytes(intByteAr, swap);
        return this;
    }

    public BinaryPacketStream writeBytes(byte[] bytes) {
        writeBytes(bytes, this.swap);
        return this;
    }

    public BinaryPacketStream writeBytes(byte[] bytes, boolean swap) {
        if (swap)
            bytes = byteArrayReverse(bytes);
        reserve(count + bytes.length);

        System.arraycopy(bytes, 0, this.buf, count, bytes.length);

        this.count += bytes.length;
        return this;
    }

    public BinaryPacketStream writeByte(byte b) {
        reserve(count + 1);
        buf[count++] = b;
        return this;
    }


    public long readCUint() {
        if (!canRead(1))
            throw new BufferUnderflowException();


        return switch (buf[pointer] & 0xE0) {
            case 0xE0 -> {
                readByte();
                yield readUInt(true);
            }
            case 0xC0 -> readUInt(true) & 0x3FFFFFFF;
            case 0x80, 0xA0 -> readUShort(true) & 0x7FFF;
            default -> readByte();
        };
    }


    public int readUShort(boolean swap) {
        byte[] ar = readBytes(2, swap);
        int b1 = ar[0] & 0xFF;
        int b2 = ar[1] & 0xFF;

        return (b1 << 8 | b2) & 0xFFFF;
    }

    public long readUInt() {
        return readUInt(this.swap);
    }

    public long readUInt(boolean swap) {
        byte[] ar = readBytes(4, swap);
        int b1 = ar[0] & 0xFF;
        int b2 = ar[1] & 0xFF;
        int b3 = ar[2] & 0xFF;
        int b4 = ar[3] & 0xFF;

        return ((long) b1 << 24 | b2 << 16 | b3 << 8 | b4) & 0xFFFFFFFFL;
    }

    public int readInt() {
        return readInt(this.swap);
    }

    public int readInt(boolean swap) {
        return (int) readUInt(swap);
    }

    public byte[] readBytes(int len) {
        return readBytes(len, this.swap);
    }

    public byte[] readBytes(long len) {
        return readBytes((int) len, this.swap);
    }

    public byte[] readBytes(long len, boolean swap) {

        return readBytes((int) len, swap);
    }

    public byte[] readBytes(int len, boolean swap) {
//        if (!canRead(len))
//            throw new BufferUnderflowException();

        byte[] ret = new byte[len];
        for (int i = 0; i < len; i++) {
            ret[i] = readByte();
        }

        return swap ? ret : byteArrayReverse(ret);
    }

    public byte readByte() {
        return buf[pointer++];
    }

    public char readUByte() {
        return (char) (buf[pointer++] & 0xFF);
    }


    public static byte[] byteArrayReverse(byte[] ar) {
        for (int i = 0; i < ar.length / 2; i++) {
            byte temp = ar[i];
            ar[i] = ar[ar.length - i - 1];
            ar[ar.length - i - 1] = temp;
        }
        return ar;
    }

    public void skip(int length) {
        reserve(length);
        this.pointer += length;
    }

    public boolean canRead(int length) {
        return pointer + length < buf.length;
    }

    private void expand() {
        this.buf = Arrays.copyOf(buf, buf.length * 2);
    }

    private void reserve(int count) {
        if (this.buf == null)
            this.buf = new byte[10];

        if (count > buf.length)
            expand();
    }
}


