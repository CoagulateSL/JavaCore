package net.coagulate.Core.Exceptions.User;

/** User input is too long */

public class UserInputDuplicateValueException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputDuplicateValueException(final String reason) {
		super(reason);
	}

	public UserInputDuplicateValueException(final String reason, final Throwable cause) {
		super(reason, cause);
	}
}
