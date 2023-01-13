package net.coagulate.Core.Exceptions.User;

import java.io.Serial;

/**
 * User not allowed to access this
 */

public class UserAccessDeniedException extends UserInputValidationException {
	@Serial private static final long serialVersionUID=1L;
	
	public UserAccessDeniedException(final String message) {
		super(message);
	}
	
	public UserAccessDeniedException(final String reason,final Throwable cause,final boolean suppresslogging) {
		super(reason,cause,suppresslogging);
	}
	
	public UserAccessDeniedException(final String reason,final boolean suppresslogging) {
		super(reason,suppresslogging);
	}
	
	public UserAccessDeniedException(final String message,final Throwable cause) {
		super(message,cause);
	}
}
