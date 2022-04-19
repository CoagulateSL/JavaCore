package net.coagulate.Core.Exceptions;

import java.io.Serial;

/**
 * Checked exception indicating an error with a users request.
 * These should be intercepted by the interface and shown appropriately.
 *
 * @author Iain Price
 */
public abstract class UserException extends RuntimeException {
	@Serial
    private static final long serialVersionUID = 1L;
	private final boolean suppresslogging;

	protected UserException(final String message) {
		super(message);
		suppresslogging = false;
	}

	protected UserException(final String message,
							final Throwable cause) {
		super(message, cause);
		if (UserException.class.isAssignableFrom(cause.getClass())) {
			suppresslogging = ((UserException) cause).suppressed();
		} else {
			if (SystemException.class.isAssignableFrom(cause.getClass())) {
				suppresslogging = ((SystemException) cause).suppressed();
			} else {
				suppresslogging = false;
			}
		}
	}

	protected UserException(final String reason,
							final boolean suppresslogging) {
		super(reason);
		this.suppresslogging = suppresslogging;
	}

	protected UserException(final String reason,
							final Throwable cause,
							final boolean suppresslogging) {
		super(reason, cause);
		this.suppresslogging = suppresslogging;
	}

	// ---------- INSTANCE ----------
	public final boolean suppressed() {
		return suppresslogging;
	}
}
