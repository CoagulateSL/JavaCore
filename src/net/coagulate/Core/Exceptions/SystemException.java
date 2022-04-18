package net.coagulate.Core.Exceptions;

/**
 * Internal errors in the code.
 * Unchecked, usually thrown to the top and logged.
 * Users are not shown these messages nor can they correct the issue.
 *
 * @author Iain Price
 */
public abstract class SystemException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final boolean suppresslogging;

	protected SystemException(final String reason) {
		super(reason);
		suppresslogging = false;
	}

	protected SystemException(final String reason,
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

	protected SystemException(final String reason,
							  final boolean suppresslogging) {
		super(reason);
		this.suppresslogging = suppresslogging;
	}

	protected SystemException(final String reason,
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
