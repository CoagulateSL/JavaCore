package net.coagulate.Core.Exceptions.User;

/**
 * User supplied no data when they should have
 */

public class UserInputEmptyException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputEmptyException(final String reason) {
		super(reason);
	}

	public UserInputEmptyException(final String reason,
	                               final Throwable cause) {
		super(reason,cause);
	}

	public UserInputEmptyException(String message, Throwable exception, boolean suppress) {
		super(message, exception, suppress);
	}

	public UserInputEmptyException(String reason, boolean suppress) {
		super(reason, suppress);
	}
}
