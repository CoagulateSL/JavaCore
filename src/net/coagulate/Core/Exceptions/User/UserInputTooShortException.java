package net.coagulate.Core.Exceptions.User;

import java.io.Serial;

/**
 * User input is too short
 */

public class UserInputTooShortException extends UserInputValidationException {
	@Serial private static final long serialVersionUID=1L;
	
	public UserInputTooShortException(final String message) {
		super(message);
	}
	
	public UserInputTooShortException(final String message,final Throwable cause) {
		super(message,cause);
	}
}
