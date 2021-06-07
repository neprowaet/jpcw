package neprowaet.jpcw.net.security;

public class RC4 extends Security{
    private byte[] table = new byte[256];
    private int x;
    private int y;

    public RC4(String key) throws NullPointerException {
        this(key.getBytes());
    }

    public RC4(byte[] key) throws NullPointerException {

        for (int i = 0; i < 256; i++) {
            table[i] = (byte) i;
        }

        x = 0;
        y = 0;

        int index1 = 0;
        int index2 = 0;

        byte tmp;

        if (key == null || key.length == 0) {
            throw new NullPointerException();
        }

        for (int i = 0; i < 256; i++) {

            index2 = ((key[index1] & 0xff) + (table[i] & 0xff) + index2) & 0xff;

            tmp = table[i];
            table[i] = table[index2];
            table[index2] = tmp;

            index1 = (index1 + 1) % key.length;
        }

    }
    @Override
    public Byte[] unpack(byte b) {
        x = (x + 1) & 0xff;
        y = (y + (table[x] & 0xff)) & 0xff;

        byte tmp = table[x];
        table[x] = table[y];
        table[y] = tmp;

        int xorIndex = ((table[x] & 0xff) + (table[y] & 0xff)) & 0xff;
        return new Byte[] { (byte) (b ^ table[xorIndex]) };
    }
}
