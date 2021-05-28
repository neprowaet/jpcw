package neprowaet.jpcw.net.security;


import java.util.ArrayList;
import java.util.List;


public class MPPC extends Security{

    private int code1;
    private int code2;
    private int code3;
    private int code4;

    private byte packedOffset;

    private ArrayList<Integer> packedBytes;
    private ArrayList<Integer> unpackedBytes;

    public MPPC()
    {
        code3 = 0;
        code4 = 0;

        packedBytes = new ArrayList<>();
        unpackedBytes = new ArrayList<>();
    }

    public Byte[] unpack(byte packedByte)
    {
        packedBytes.add(packedByte & 0xff);

        ArrayList<Byte> unpackedChunk = new ArrayList<>();

        if (unpackedBytes.size() >= 10240)
            unpackedBytes.subList(0, 2048).clear();
        
        for (; ; ) {
            if (code3 == 0) {
                if (hasBits(4)) {
                    if (getPackedBits(1) == 0) {
                        // 0-xxxxxxx
                        code1 = 1;
                        code3 = 1;
                    }
                    else {
                        if (getPackedBits(1) == 0) {
                            // 10-xxxxxxx
                            code1 = 2;
                            code3 = 1;
                        }
                        else {
                            if (getPackedBits(1) == 0) {
                                // 110-xxxxxxxxxxxxx-*
                                code1 = 3;
                                code3 = 1;
                            } else {
                                if (getPackedBits(1) == 0) {
                                    // 1110-xxxxxxxx-*
                                    code1 = 4;
                                    code3 = 1;
                                } else {
                                    // 1111-xxxxxx-*
                                    code1 = 5;
                                    code3 = 1;
                                }
                            }
                        }
                    }
                }
                else
                    break;
            }
            else if (code3 == 1) {
                if (code1 == 1) {
                    if (hasBits(7)) {
                        byte outB = (byte)(getPackedBits(7) & 0xff);
                        unpackedChunk.add(outB);
                        unpackedBytes.add(outB & 0xff);
                        code3 = 0;
                    }
                    else
                        break;
                }
                else if (code1 == 2) {
                    if (hasBits(7)) {
                        
                        byte outB = (byte)((getPackedBits(7) | 0x80) & 0xff);
                        unpackedChunk.add(outB);
                        unpackedBytes.add(outB & 0xff);
                        code3 = 0;
                    }
                    else
                        break;
                }
                else if (code1 == 3) {
                    if (hasBits(13)) {
                        code4 = (int)getPackedBits(13) + 0x140;
                        code3 = 2;
                    }
                    else
                        break;
                }
                else if (code1 == 4) {
                    if (hasBits(8)) {
                        code4 = (int)getPackedBits(8) + 0x40;
                        code3 = 2;
                    }
                    else
                        break;
                }
                else if (code1 == 5) {
                    if (hasBits(6)) {
                        code4 = (int)getPackedBits(6);
                        code3 = 2;
                    }
                    else
                        break;
                }
            }
            else if (code3 == 2) {
                if (code4 == 0) {
                    // Guess !!!
                    if ((packedOffset & 0xff) != 0) {
                        packedOffset = 0;
                        packedBytes.remove(0);
                    }
                    code3 = 0;
                    continue;
                }
                code2 = 0;
                code3 = 3;
            }
            else if (code3 == 3) {
                if (hasBits(1)) {
                    if (getPackedBits(1) == 0) {
                        code3 = 4;
                    }
                    else {
                        code2++;
                    }
                }
                else
                    break;
            }
            else if (code3 == 4) {
                int copySize;

                if (code2 == 0) {
                    copySize = 3;
                }
                else {
                    int size = code2 + 1;

                    if (hasBits(size)) {
                        copySize = (int)((getPackedBits(size) + (1 << size)));
                    }
                    else
                        break;
                }

                copy(code4, copySize, unpackedChunk);
                code3 = 0;
            }
        }
        return unpackedChunk.toArray(new Byte[0]);
    }

//    public List<Integer> unpack(byte[] compressedBytes) {
//        ArrayList<Integer> rtnList = new ArrayList<Integer>(compressedBytes.length);
//
//        for (byte b : compressedBytes) {
//            rtnList.addAll(unpack(b));
//        }
//
//        return rtnList;
//    }

    private void copy(int shift, int size, List<Byte> unpackedChunkData) {
        for (int i = 0; i < size; i++) {
            int pIndex = unpackedBytes.size() - shift;

            if (pIndex < 0)
                return;

            int b = unpackedBytes.get(pIndex) & 0xff;
            unpackedBytes.add(b);
            unpackedChunkData.add((byte)b);
        }
    }



    private long getPackedBits(int bitCount) {

        if (bitCount > 16)
            return 0;

        int alBitCount = bitCount + (packedOffset & 0xff);
        int alByteCount = (alBitCount + 7) / 8;

        long v = 0;

        for (int i = 0; i < alByteCount; i++)
        {
            v = (v | (((packedBytes.get(i) & 0xffffffffL) << (24 - i * 8))) & 0xffffffffL);
            v &= 0xffffffffL;
        }
        
        v <<= packedOffset;
        v &= 0xffffffffL;
        v >>>= (32 - bitCount) & 0xffffffffL;

        packedOffset += bitCount & 0xff;
        int freeBytes = (packedOffset & 0xff) / 8;

        if (freeBytes != 0)
            packedBytes.subList(0, freeBytes).clear();

        packedOffset %= 8;

        return v & 0xffffffffL;
    }

    private boolean hasBits(int count)
    {
        return (packedBytes.size() * 8 - (packedOffset & 0xff)) >= count;

    }
}

