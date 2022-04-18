package net.coagulate.Core.Exceptions.User;

import java.io.Serial;

/**
 * User input makes no sense given the current state
 */

public class UserInputStateException extends UserInputValidationException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserInputStateException(final String message) {
        super(message);
    }

    public UserInputStateException(final String message,
                                   final Throwable cause) {
        super(message, cause);
    }

    public UserInputStateException(final String reason, final boolean suppresslogging) {
        super(reason, suppresslogging);
    }

    public UserInputStateException(final String reason,
                                   final Throwable cause,
                                   final boolean suppresslogging) {
        super(reason, cause, suppresslogging);
    }
}
