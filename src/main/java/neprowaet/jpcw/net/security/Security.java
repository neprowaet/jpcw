package neprowaet.jpcw.net.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Security {
    public abstract Byte[] unpack(byte b);

    public byte[] unpack(byte[] buffer) {
        List<Byte> toReturn = new ArrayList<>();
        for (byte b : buffer) {
            toReturn.addAll(Arrays.asList(unpack(b)));
        }

        byte[] ret = new byte[toReturn.size()];
        for (int i = 0; i < toReturn.size(); i++) {
            ret[i] = toReturn.get(i);
        }

        return ret;
    }
}
