package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

import java.io.Serial;

/**
 * Some resource unexpectedly failed to initialise
 */

public class SystemInitialisationException extends SystemException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SystemInitialisationException(final String message) {
        super(message);
    }

    public SystemInitialisationException(final String message,
                                         final Throwable cause) {
        super(message, cause);
    }
}
