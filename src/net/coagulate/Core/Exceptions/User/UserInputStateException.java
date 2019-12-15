package net.coagulate.Core.Exceptions.User;

/** User input makes no sense given the current state */

public class UserInputStateException extends UserInputValidationException {
	private static final long serialVersionUID=1L;
	public UserInputStateException(String reason) {
		super(reason);
	}

	public UserInputStateException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
