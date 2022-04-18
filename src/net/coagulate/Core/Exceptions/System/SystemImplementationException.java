package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

/**
 * An error in the implementation of something, probably code
 */

public class SystemImplementationException extends SystemException {
    private static final long serialVersionUID = 1L;

    public SystemImplementationException(final String message) {
        super(message);
    }

    public SystemImplementationException(final String message,
                                         final Throwable cause) {
        super(message, cause);
    }
}
