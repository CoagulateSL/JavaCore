package net.coagulate.Core.Exceptions.User;

/** User input doesn't parse correctly */

public class UserInputValidationParseException extends UserInputValidationException {
	private static final long serialVersionUID=1L;
	public UserInputValidationParseException(String reason) {
		super(reason);
	}

	public UserInputValidationParseException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
