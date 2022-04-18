package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

/**
 * User input fails to validate.  subclass me :)
 */

public abstract class UserInputValidationException extends UserException {
	private static final long serialVersionUID = 1L;

	protected UserInputValidationException(final String reason) {
		super(reason);
	}

	protected UserInputValidationException(final String reason,
										   final Throwable cause) {
		super(reason, cause);
	}

	protected UserInputValidationException(final String message,
										   final Throwable exception,
										   final boolean suppress) {
		super(message, exception, suppress);
	}

	protected UserInputValidationException(final String reason, final boolean suppress) {
		super(reason, suppress);
	}
}
