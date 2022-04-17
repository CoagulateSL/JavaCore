package net.coagulate.Core.Exceptions.User;

/**
 * User input doesn't parse correctly
 */

public class UserInputValidationFilterException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputValidationFilterException(final String reason) {
		super(reason);
	}

	public UserInputValidationFilterException(final String reason,
	                                          final Throwable cause) {
		super(reason,cause);
	}

    public UserInputValidationFilterException(final String reason, final boolean suppress) {
        super(reason, suppress);
    }
}
