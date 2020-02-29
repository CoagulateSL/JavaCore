package net.coagulate.Core.Exceptions.User;

/**
 * User input makes no sense given the current state
 */

public class UserInputStateException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputStateException(final String reason) {
		super(reason);
	}

	public UserInputStateException(final String reason,
	                               final Throwable cause) {
		super(reason,cause);
	}

	public UserInputStateException(String message,
	                               Throwable exception,
	                               boolean suppress) {super(message,exception,suppress);}
}
