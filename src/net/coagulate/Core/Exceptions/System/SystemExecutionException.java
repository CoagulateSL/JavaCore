package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

/**
 * A value was read that made no sense and shouldn't have happened
 */

public class SystemExecutionException extends SystemException {
    private static final long serialVersionUID = 1L;

    public SystemExecutionException(final String message) {
        super(message);
    }

    public SystemExecutionException(final String message,
                                    final Throwable cause) {
        super(message, cause);
    }
}
