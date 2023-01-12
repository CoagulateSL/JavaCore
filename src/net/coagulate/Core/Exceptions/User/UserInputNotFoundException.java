package net.coagulate.Core.Exceptions.User;

import java.io.Serial;

/**
 * User supplied no data when they should have
 */

public class UserInputNotFoundException extends UserInputValidationException {
	@Serial private static final long serialVersionUID=1L;
	
	public UserInputNotFoundException(final String reason,final Throwable cause,final boolean suppresslogging) {
		super(reason,cause,suppresslogging);
	}
	
	public UserInputNotFoundException(final String reason,final boolean suppresslogging) {
		super(reason,suppresslogging);
	}
	
	public UserInputNotFoundException(final String message) {
		super(message);
	}
	
	public UserInputNotFoundException(final String message,final Throwable cause) {
		super(message,cause);
	}
}
