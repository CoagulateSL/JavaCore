package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;

/**
 * @author Iain Price
 */
public class NumberTools {
	private static final boolean debug=false;

	// ---------- STATICS ----------
	@Nonnull
	public static String fixdp(final float number,
	                           final int dp) {
		if (debug) { System.out.println("SRC "+number+" for dp "+dp+" = "+Math.pow(10,dp)); }
		final int whole=(int) Math.round(Math.floor(number));
		if (debug) { System.out.println("whole:"+whole); }
		final int decimal=Math.round((number- whole)*((float) (Math.pow(10,dp))));
		if (debug) { System.out.println("dec:"+decimal); }
		final StringBuilder decimalstring = new StringBuilder(String.valueOf(decimal));
		if (debug) { System.out.println("decimalstring:"+decimalstring); }
		while (decimalstring.length()<dp) { decimalstring.append("0"); }
		if (debug) { System.out.println("decimalstring:"+decimalstring); }
		final String out=whole+"."+decimalstring;
		if (debug) { System.out.println("OUT "+out); }
		return out;
	}

}
