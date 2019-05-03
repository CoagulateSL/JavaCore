package net.coagulate.Core.Tools;

/**
 * @author Iain Price
 */
public abstract class Tokens {

	private static final String tokenfont = "0123456789abdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public static String generateToken() {
		String token = "";
		while (token.length() < 64) {
			token = token + tokenfont.charAt((int) (Math.random() * tokenfont.length()));
		}
		return token;
	}

}
