package net.coagulate.Core.Exceptions.System;

/** An external system failed something */

import net.coagulate.Core.Exceptions.SystemException;

public class SystemRemoteFailureException extends SystemException {
	private static final long serialVersionUID=1L;

	public SystemRemoteFailureException(String reason) {
		super(reason);
	}

	public SystemRemoteFailureException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
