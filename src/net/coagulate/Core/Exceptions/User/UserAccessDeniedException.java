package net.coagulate.Core.Exceptions.User;

/**
 * User not allowed to access this
 */

public class UserAccessDeniedException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserAccessDeniedException(final String reason) {
		super(reason);
	}

	public UserAccessDeniedException(final String reason,
	                                 final Throwable cause) {
		super(reason,cause);
	}
}
