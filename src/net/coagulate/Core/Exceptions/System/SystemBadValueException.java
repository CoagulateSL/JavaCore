package net.coagulate.Core.Exceptions.System;

import net.coagulate.Core.Exceptions.SystemException;

/** A value was read that made no sense and shouldn't have happened */

public class SystemBadValueException extends SystemException {
	private static final long serialVersionUID=1L;

	public SystemBadValueException(final String reason) {
		super(reason);
	}

	public SystemBadValueException(final String reason, final Throwable cause) {
		super(reason, cause);
	}
}
