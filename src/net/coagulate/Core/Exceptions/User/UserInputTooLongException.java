package net.coagulate.Core.Exceptions.User;

/**
 * User input is too long
 */

public class UserInputTooLongException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputTooLongException(final String reason) {
		super(reason);
	}

	public UserInputTooLongException(final String reason,
	                                 final Throwable cause) {
		super(reason,cause);
	}
}
