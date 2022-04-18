package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

/**
 * User attempted to lookup something that doesn't exist
 */

public class UserInsufficientCreditException extends UserException {
    private static final long serialVersionUID = 1L;

    public UserInsufficientCreditException(final String message) {
        super(message);
    }

    public UserInsufficientCreditException(final String message,
                                           final Throwable cause) {
        super(message, cause);
    }
}
