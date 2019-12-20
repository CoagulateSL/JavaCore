package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

/**
 * An error in the implementation of something, probably code
 */

public class SystemImplementationException extends SystemException {
	private static final long serialVersionUID=1L;

	public SystemImplementationException(final String reason) {
		super(reason);
	}

	public SystemImplementationException(final String reason,
	                                     final Throwable cause) {
		super(reason,cause);
	}
}
