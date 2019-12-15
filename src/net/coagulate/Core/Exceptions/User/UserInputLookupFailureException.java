package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

/** User attempted to lookup something that doesn't exist */

public class UserInputLookupFailureException extends UserException {
	private static final long serialVersionUID=1L;

	public UserInputLookupFailureException(String reason) {
		super(reason);
	}

	public UserInputLookupFailureException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
