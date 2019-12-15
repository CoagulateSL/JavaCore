package net.coagulate.Core.Exceptions.User;

/** User input is too long */

public class UserInputDuplicateValueException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputDuplicateValueException(String reason) {
		super(reason);
	}

	public UserInputDuplicateValueException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
