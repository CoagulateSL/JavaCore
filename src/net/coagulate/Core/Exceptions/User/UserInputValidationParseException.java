package net.coagulate.Core.Exceptions.User;

import java.io.Serial;

/**
 * User input doesn't parse correctly
 */

public class UserInputValidationParseException extends UserInputValidationException {
	@Serial private static final long serialVersionUID=1L;
	
	public UserInputValidationParseException(final String message) {
		super(message);
	}
	
	public UserInputValidationParseException(final String message,final Throwable cause) {
		super(message,cause);
	}
	
	public UserInputValidationParseException(final String reason,final boolean suppresslogging) {
		super(reason,suppresslogging);
	}
	
	public UserInputValidationParseException(final String reason,final Throwable cause,final boolean suppresslogging) {
		super(reason,cause,suppresslogging);
	}
}
