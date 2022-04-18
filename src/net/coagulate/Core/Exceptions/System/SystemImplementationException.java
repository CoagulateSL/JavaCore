package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

import java.io.Serial;

/**
 * An error in the implementation of something, probably code
 */

public class SystemImplementationException extends SystemException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SystemImplementationException(final String message) {
        super(message);
    }

    public SystemImplementationException(final String message,
                                         final Throwable cause) {
        super(message, cause);
    }
}
