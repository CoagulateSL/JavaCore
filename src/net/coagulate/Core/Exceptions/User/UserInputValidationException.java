package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

import java.io.Serial;

/**
 * User input fails to validate.  subclass me :)
 */

public abstract class UserInputValidationException extends UserException {
	@Serial private static final long serialVersionUID=1L;
	
	protected UserInputValidationException(final String message) {
		super(message);
	}
	
	protected UserInputValidationException(final String message,final Throwable cause) {
		super(message,cause);
	}
	
	protected UserInputValidationException(final String reason,final Throwable cause,final boolean suppresslogging) {
		super(reason,cause,suppresslogging);
	}
	
	protected UserInputValidationException(final String reason,final boolean suppresslogging) {
		super(reason,suppresslogging);
	}
}
