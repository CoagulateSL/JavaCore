package net.coagulate.Core.Exceptions.User;

/** User supplied no data when they should have */

public class UserInputEmptyException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputEmptyException(String reason) {
		super(reason);
	}

	public UserInputEmptyException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
