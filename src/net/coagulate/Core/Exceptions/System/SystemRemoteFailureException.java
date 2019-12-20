package net.coagulate.Core.Exceptions.System;


import net.coagulate.Core.Exceptions.SystemException;

/**
 * An external system failed something
 */
public class SystemRemoteFailureException extends SystemException {
	private static final long serialVersionUID=1L;

	public SystemRemoteFailureException(final String reason) {
		super(reason);
	}

	public SystemRemoteFailureException(final String reason,
	                                    final Throwable cause) {
		super(reason,cause);
	}
}
