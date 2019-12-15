package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;

/**
 * @author Iain Price
 */
public abstract class Tokens {

	private static final String tokenfont = "0123456789abdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	@Nonnull
	public static String generateToken() {
		final StringBuilder token = new StringBuilder();
		while (token.length() < 64) {
			token.append(tokenfont.charAt((int) (Math.random() * tokenfont.length())));
		}
		return token.toString();
	}

}
