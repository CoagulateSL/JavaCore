package net.coagulate.Core.Exceptions.User;

/**
 * User supplied no data when they should have
 */

public class UserInputNotFoundException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputNotFoundException(final String reason) {
		super(reason);
	}

	public UserInputNotFoundException(final String reason,final Throwable cause) {
		super(reason,cause);
	}
}
