package neprowaet.jpcw.net.security;

public class NullSecurity extends Security {
    @Override
    public Byte[] unpack(byte b) {
        return new Byte[] { b };
    }
}
