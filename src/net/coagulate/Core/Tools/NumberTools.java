package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;

/**
 * @author Iain Price
 */
public class NumberTools {
	private static final boolean debug = false;

	@Nonnull
	public static String fixdp(float number, int dp) {
		if (debug) { System.out.println("SRC " + number + " for dp " + dp + " = " + Math.pow(10, dp)); }
		int whole = (int) Math.round(Math.floor(number));
		if (debug) { System.out.println("whole:" + whole); }
		int decimal = Math.round((number - ((float) whole)) * ((float) (Math.pow(10, dp))));
		if (debug) { System.out.println("dec:" + decimal); }
		StringBuilder decstr = new StringBuilder(decimal + "");
		if (debug) { System.out.println("decstr:" + decstr); }
		while (decstr.length() < dp) { decstr.append("0"); }
		if (debug) { System.out.println("decstr:" + decstr); }
		String out = whole + "." + decstr;
		if (debug) { System.out.println("OUT " + out); }
		return out;
	}

}
