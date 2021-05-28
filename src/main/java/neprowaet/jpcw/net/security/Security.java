package neprowaet.jpcw.net.security;

import java.util.ArrayList;
import java.util.List;

public abstract class Security {
    public abstract Byte[] unpack (byte b);

    public Byte[] unpack (byte[] buffer) {
        List<Byte> toReturn = new ArrayList<>();
        for(byte b : buffer) {
            //toReturn.add(unpack(b));
        }

        return toReturn.toArray(new Byte[0]);
    }
}
