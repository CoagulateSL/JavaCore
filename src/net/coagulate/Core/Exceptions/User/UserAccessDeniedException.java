package net.coagulate.Core.Exceptions.User;

/** User not allowed to access this */

public class UserAccessDeniedException extends UserInputValidationException {
	private static final long serialVersionUID=1L;
	public UserAccessDeniedException(String reason) {
		super(reason);
	}

	public UserAccessDeniedException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
