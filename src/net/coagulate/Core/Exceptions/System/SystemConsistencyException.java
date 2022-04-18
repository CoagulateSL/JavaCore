package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

/**
 * Internal state is not as expected, in a programming error way
 */

public class SystemConsistencyException extends SystemException {
    private static final long serialVersionUID = 1L;

    public SystemConsistencyException(final String message) {
        super(message);
    }

    public SystemConsistencyException(final String message,
                                      final Throwable cause) {
        super(message, cause);
    }
}
