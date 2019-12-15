package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

/** Tell the user a remote system failed */

public class UserRemoteFailureException extends UserException {
	private static final long serialVersionUID=1L;

	public UserRemoteFailureException(String reason) {
		super(reason);
	}

	public UserRemoteFailureException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
