package neprowaet.jpcw.net.security;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class HMACMD5HASH {

    public static byte[] getHash(String login, String password, byte[] key) {
        try {
            byte[] loginData = login.getBytes();
            byte[] authData = (login + password).getBytes();

            return new HMACMD5(hex2byte(getMD5Hash(login + password)))
                    .computeHash(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    static class HMACMD5 { //c35ad8d6ad4044296690fb4158fee7a5
        Mac mac;

        public HMACMD5(byte[] key) throws InvalidKeyException, NoSuchAlgorithmException {
            SecretKeySpec signingKey = new SecretKeySpec(key, "HmacMD5");
            this.mac = Mac.getInstance("HmacMD5");
            this.mac.init(signingKey);
        }

        public byte[] computeHash(byte[] data) {
            return mac.doFinal(data);
        }

    }

    public static byte[] hex2byte(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public static String getMD5Hash(String str) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        m.update(str.getBytes(StandardCharsets.UTF_8));
        String s2 = new BigInteger(1, m.digest()).toString(16);
        StringBuilder sb = new StringBuilder(32);
        for (int i = 0, count = 32 - s2.length(); i < count; i++) {
            sb.append("0");
        }
        return sb.append(s2).toString();
    }


}
