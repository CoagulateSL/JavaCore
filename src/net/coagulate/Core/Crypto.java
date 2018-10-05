package net.coagulate.Core;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Iain Price
 */
public class Crypto {

    public static String SHA1(String string) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            return DatatypeConverter.printHexBinary(md.digest(string.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new AssertionError("No SHA-1 algorithm??");
        }
    }
    public static String SHA256(String string) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            return DatatypeConverter.printHexBinary(md.digest(string.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new AssertionError("No SHA-256 algorithm??");
        }
    }    
}
