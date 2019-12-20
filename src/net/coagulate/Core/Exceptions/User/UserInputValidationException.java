package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

/**
 * User input fails to validate.  subclass me :)
 */

public abstract class UserInputValidationException extends UserException {
	private static final long serialVersionUID=1L;

	public UserInputValidationException(final String reason) {
		super(reason);
	}

	public UserInputValidationException(final String reason,
	                                    final Throwable cause) {
		super(reason,cause);
	}
}
