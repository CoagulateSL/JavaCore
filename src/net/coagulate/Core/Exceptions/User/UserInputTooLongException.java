package net.coagulate.Core.Exceptions.User;

/** User input is too long */

public class UserInputTooLongException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputTooLongException(String reason) {
		super(reason);
	}

	public UserInputTooLongException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
