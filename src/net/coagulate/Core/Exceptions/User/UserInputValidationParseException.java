package net.coagulate.Core.Exceptions.User;

/**
 * User input doesn't parse correctly
 */

public class UserInputValidationParseException extends UserInputValidationException {
	private static final long serialVersionUID=1L;

	public UserInputValidationParseException(final String reason) {
		super(reason);
	}

	public UserInputValidationParseException(final String reason,
	                                         final Throwable cause) {
		super(reason,cause);
	}

    public UserInputValidationParseException(final String reason, final boolean suppress) {
        super(reason, suppress);
    }

    public UserInputValidationParseException(final String reason, final Throwable cause, final boolean suppress) {
        super(reason, cause, suppress);
    }
}
