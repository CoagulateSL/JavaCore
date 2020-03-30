package net.coagulate.Core.Exceptions;

/**
 * Internal errors in the code.
 * Unchecked, usually thrown to the top and logged.
 * Users are not shown these messages nor can they correct the issue.
 *
 * @author Iain Price
 */
public abstract class SystemException extends RuntimeException {
	private static final long serialVersionUID=1L;
	private boolean suppresslogging=false;

	public SystemException(final String reason) { super(reason); }

	public SystemException(final String reason,
	                       final Throwable cause) { super(reason,cause); }

	public SystemException(final String reason,
	                       final boolean suppresslogging) {
		super(reason);
		this.suppresslogging=suppresslogging;
	}

	public SystemException(final String reason,
	                       final Throwable cause,
	                       final boolean suppresslogging) {
		super(reason,cause);
		this.suppresslogging=suppresslogging;
	}

	public final boolean suppressed() { return suppresslogging; }
}
