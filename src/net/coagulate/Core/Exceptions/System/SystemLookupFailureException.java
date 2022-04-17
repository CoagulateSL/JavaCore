package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

/**
 * A lookup failed that was expected to succeed
 */

public class SystemLookupFailureException extends SystemException {
	private static final long serialVersionUID=1L;

	public SystemLookupFailureException(final String reason) {
		super(reason);
	}

	public SystemLookupFailureException(final String reason, final boolean suppresslogging) {
        super(reason, suppresslogging);
    }

    public SystemLookupFailureException(final String reason, final Throwable cause, final boolean suppresslogging) {
        super(reason, cause, suppresslogging);
    }

	public SystemLookupFailureException(final String reason,
										final Throwable cause) {
		super(reason,cause);
	}
}
