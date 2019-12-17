package net.coagulate.Core.Exceptions.System;

/** An external system failed something */

import net.coagulate.Core.Exceptions.SystemException;

public class SystemRemoteFailureException extends SystemException {
	private static final long serialVersionUID=1L;

	public SystemRemoteFailureException(final String reason) {
		super(reason);
	}

	public SystemRemoteFailureException(final String reason, final Throwable cause) {
		super(reason, cause);
	}
}
