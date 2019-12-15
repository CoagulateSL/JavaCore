package net.coagulate.Core.Exceptions.User;

/** User input is too short */

public class UserInputInvalidChoiceException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputInvalidChoiceException(String reason) {
		super(reason);
	}

	public UserInputInvalidChoiceException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
