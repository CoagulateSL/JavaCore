package net.coagulate.Core.Exceptions.System;


import net.coagulate.Core.Exceptions.SystemException;

import java.io.Serial;

/**
 * An external system failed something
 */
public class SystemRemoteFailureException extends SystemException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SystemRemoteFailureException(final String message) {
        super(message);
    }

    public SystemRemoteFailureException(final String message,
                                        final Throwable cause) {
        super(message, cause);
    }
}
