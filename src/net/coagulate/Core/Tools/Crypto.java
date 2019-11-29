package net.coagulate.Core.Tools;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Iain Price
 */
public abstract class Crypto {

	public static String SHA1(String string) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
			return DatatypeConverter.printHexBinary(md.digest(string.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("No SHA-1 algorithm??");
		}
	}

	public static String SHA256(String string) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-256");
			return DatatypeConverter.printHexBinary(md.digest(string.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("No SHA-256 algorithm??");
		}
	}
}
