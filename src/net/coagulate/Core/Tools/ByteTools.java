package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;
import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Iain Price
 */
public abstract class ByteTools {

	private static String toBase64(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}

	@Nonnull
	public static String convertStreamToString(@Nonnull InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	private static byte[] fromBase64(String hex) {
		return DatatypeConverter.parseBase64Binary(hex);
	}

}
