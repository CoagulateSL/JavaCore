package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

import java.io.Serial;

/**
 * A lookup failed that was expected to succeed
 */

public class SystemLookupFailureException extends SystemException {
	@Serial private static final long serialVersionUID=1L;
	
	public SystemLookupFailureException(final String message) {
		super(message);
	}
	
	public SystemLookupFailureException(final String reason,final boolean suppresslogging) {
		super(reason,suppresslogging);
	}
	
	public SystemLookupFailureException(final String reason,final Throwable cause,final boolean suppresslogging) {
		super(reason,cause,suppresslogging);
	}
	
	public SystemLookupFailureException(final String message,final Throwable cause) {
		super(message,cause);
	}
}
