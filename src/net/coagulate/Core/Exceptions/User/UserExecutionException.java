package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

import java.io.Serial;

/**
 * The user configuration makes no sense
 */

public class UserExecutionException extends UserException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserExecutionException(final String message) {
        super(message);
    }

    public UserExecutionException(final String message,
                                  final Throwable cause) {
        super(message, cause);
    }
}
