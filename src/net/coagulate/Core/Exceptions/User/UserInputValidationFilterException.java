package net.coagulate.Core.Exceptions.User;

/**
 * User input doesn't parse correctly
 */

public class UserInputValidationFilterException extends UserInputValidationException {
    private static final long serialVersionUID = 1L;

    public UserInputValidationFilterException(final String message) {
        super(message);
    }

    public UserInputValidationFilterException(final String message,
                                              final Throwable cause) {
        super(message, cause);
    }

    public UserInputValidationFilterException(final String reason, final boolean suppresslogging) {
        super(reason, suppresslogging);
    }
}
