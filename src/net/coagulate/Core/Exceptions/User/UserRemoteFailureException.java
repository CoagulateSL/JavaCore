package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

/**
 * Tell the user a remote system failed
 */

public class UserRemoteFailureException extends UserException {
	private static final long serialVersionUID=1L;

	public UserRemoteFailureException(final String reason) {
		super(reason);
	}

	public UserRemoteFailureException(final String reason,
	                                  final Throwable cause) {
		super(reason,cause);
	}

	public UserRemoteFailureException(String reason, boolean suppresslogging) {
		super(reason, suppresslogging);
	}

	public UserRemoteFailureException(String reason, Throwable cause, boolean suppresslogging) {
		super(reason, cause, suppresslogging);
	}
}
