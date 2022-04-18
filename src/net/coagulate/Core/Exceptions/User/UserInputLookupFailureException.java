package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

import java.io.Serial;

/**
 * User attempted to lookup something that doesn't exist
 */

public class UserInputLookupFailureException extends UserException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserInputLookupFailureException(final String message) {
        super(message);
    }

    public UserInputLookupFailureException(final String message,
                                           final Throwable cause) {
        super(message, cause);
    }

    public UserInputLookupFailureException(final String reason,
                                           final boolean suppresslogging) {
        super(reason, suppresslogging);
    }

    public UserInputLookupFailureException(final String reason,
                                           final Throwable cause,
                                           final boolean suppresslogging) {
        super(reason, cause, suppresslogging);
    }
}
