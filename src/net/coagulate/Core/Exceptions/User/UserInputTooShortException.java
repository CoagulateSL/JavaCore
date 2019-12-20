package net.coagulate.Core.Exceptions.User;

/**
 * User input is too short
 */

public class UserInputTooShortException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputTooShortException(final String reason) {
		super(reason);
	}

	public UserInputTooShortException(final String reason,
	                                  final Throwable cause) {
		super(reason,cause);
	}
}
