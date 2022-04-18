package net.coagulate.Core.Exceptions;

/**
 * Checked exception indicating an error with a users request.
 * These should be intercepted by the interface and shown appropriately.
 *
 * @author Iain Price
 */
public abstract class UserException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final boolean suppresslogging;

	protected UserException(final String reason) {
		super(reason);
		suppresslogging = false;
	}

	protected UserException(final String reason,
							final Throwable cause) {
		super(reason, cause);
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
