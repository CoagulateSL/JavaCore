package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

/**
 * Some resource unexpectedly failed to initialise
 */

public class SystemInitialisationException extends SystemException {
	private static final long serialVersionUID=1L;

	public SystemInitialisationException(final String reason) {
		super(reason);
	}

	public SystemInitialisationException(final String reason,
	                                     final Throwable cause) {
		super(reason,cause);
	}
}
