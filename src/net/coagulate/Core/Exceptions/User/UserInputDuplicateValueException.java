package net.coagulate.Core.Exceptions.User;

/**
 * User input is too long
 */

public class UserInputDuplicateValueException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputDuplicateValueException(final String reason) {
		super(reason);
	}

	public UserInputDuplicateValueException(String message, Throwable exception, boolean suppress) {
		super(message, exception, suppress);
	}

	public UserInputDuplicateValueException(String reason, boolean suppress) {
		super(reason, suppress);
	}

	public UserInputDuplicateValueException(final String reason,
											final Throwable cause) {
		super(reason,cause);
	}
}
