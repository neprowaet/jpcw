package neprowaet.jpcw.io;

import jdk.jfr.Unsigned;

import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collections;

public class BinaryPacketStream {

    private byte[] buf = new byte[16];

    private int pointer = 0;

    private boolean swap = false;

    public BinaryPacketStream() {
    }

    public BinaryPacketStream(byte[] ar) {
        this.buf = ar;
    }

    public BinaryPacketStream(byte[] ar, int len) {
        this.buf = Arrays.copyOf(ar, len);
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
            case 0x80, 0xA0 -> readUInt16(true) & 0x7FFF;
            default -> readByte();
        };
    }


    public int readUInt16(boolean swap) {
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
        return (int)readUInt(swap);
    }

    public byte[] readBytes(int len) {
        return readBytes(len, this.swap);
    }

    public byte[] readBytes(long len) {
        return readBytes((int)len, this.swap);
    }
    public byte[] readBytes(long len, boolean swap) {

        return readBytes((int)len, swap);
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
        pointer += length;
    }

    public boolean canRead(int length) {
        return pointer + length < buf.length;
    }

    private void expand() {
        this.buf = Arrays.copyOf(buf, buf.length * 2);
    }

    public static void main(String[] args) {

    }
}


