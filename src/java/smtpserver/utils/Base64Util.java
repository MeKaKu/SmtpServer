package smtpserver.utils;

import org.apache.commons.codec.binary.Base64;

public class Base64Util {
    //encode
    public static String EncodeBase64(byte[] data) {
        return Base64.encodeBase64String(data);
    }

    //decode
    public static String DecodeBase64(String str) {
        byte[] ResultBase= Base64.decodeBase64(str);
        return new String(ResultBase);
    }

}
