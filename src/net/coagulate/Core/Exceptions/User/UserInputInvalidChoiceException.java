package net.coagulate.Core.Exceptions.User;

/**
 * User input is too short
 */

public class UserInputInvalidChoiceException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputInvalidChoiceException(final String reason) {
		super(reason);
	}

	public UserInputInvalidChoiceException(final String reason,
	                                       final Throwable cause)
	{
		super(reason,cause);
	}
}
