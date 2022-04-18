package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

import java.io.Serial;

/**
 * Tell the user a remote system failed
 */

public class UserRemoteFailureException extends UserException {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserRemoteFailureException(final String message) {
        super(message);
    }

    public UserRemoteFailureException(final String message,
                                      final Throwable cause) {
        super(message, cause);
    }

    public UserRemoteFailureException(final String reason, final boolean suppresslogging) {
        super(reason, suppresslogging);
    }

    public UserRemoteFailureException(final String reason, final Throwable cause, final boolean suppresslogging) {
        super(reason, cause, suppresslogging);
    }
}
