package net.coagulate.Core.Exceptions.User;

/**
 * User supplied no data when they should have
 */

public class UserInputEmptyException extends UserInputValidationException {
    private static final long serialVersionUID = 1L;

    public UserInputEmptyException(final String message) {
        super(message);
    }

    public UserInputEmptyException(final String message,
                                   final Throwable cause) {
        super(message, cause);
    }

    public UserInputEmptyException(final String reason, final Throwable cause, final boolean suppresslogging) {
        super(reason, cause, suppresslogging);
    }

    public UserInputEmptyException(final String reason, final boolean suppresslogging) {
        super(reason, suppresslogging);
    }
}
