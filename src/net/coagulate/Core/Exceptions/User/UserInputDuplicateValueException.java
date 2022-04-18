package net.coagulate.Core.Exceptions.User;

import java.io.Serial;

/**
 * User input is too long
 */

public class UserInputDuplicateValueException extends UserInputValidationException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserInputDuplicateValueException(final String message) {
        super(message);
    }

    public UserInputDuplicateValueException(final String reason, final Throwable cause, final boolean suppresslogging) {
        super(reason, cause, suppresslogging);
    }

    public UserInputDuplicateValueException(final String reason, final boolean suppresslogging) {
        super(reason, suppresslogging);
    }

    public UserInputDuplicateValueException(final String message,
                                            final Throwable cause) {
        super(message, cause);
    }
}
