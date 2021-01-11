package net.coagulate.Core.Exceptions.User;

/**
 * User supplied no data when they should have
 */

public class UserInputNotFoundException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputNotFoundException(String message, Throwable exception, boolean suppress) {
		super(message, exception, suppress);
	}

	public UserInputNotFoundException(String reason, boolean suppress) {
		super(reason, suppress);
	}

	public UserInputNotFoundException(final String reason) {
		super(reason);
	}

	public UserInputNotFoundException(final String reason,
	                                  final Throwable cause) {
		super(reason,cause);
	}
}
