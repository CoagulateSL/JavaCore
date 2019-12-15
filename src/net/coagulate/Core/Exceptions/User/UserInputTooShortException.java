package net.coagulate.Core.Exceptions.User;

/** User input is too short */

public class UserInputTooShortException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputTooShortException(String reason) {
		super(reason);
	}

	public UserInputTooShortException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
