package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

/** An error in the implementation of something, probably code */

public class SystemImplementationException extends SystemException {
	private static final long serialVersionUID=1L;

	public SystemImplementationException(String reason) {
		super(reason);
	}

	public SystemImplementationException(String reason, Throwable cause) {
		super(reason, cause);
	}
}
