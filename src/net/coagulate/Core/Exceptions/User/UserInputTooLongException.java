package net.coagulate.Core.Exceptions.User;

import java.io.Serial;

/**
 * User input is too long
 */

public class UserInputTooLongException extends UserInputValidationException {
	@Serial private static final long serialVersionUID=1L;
	
	public UserInputTooLongException(final String message) {
		super(message);
	}
	
	public UserInputTooLongException(final String message,final Throwable cause) {
		super(message,cause);
	}
}
