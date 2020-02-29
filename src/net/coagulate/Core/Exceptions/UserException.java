package net.coagulate.Core.Exceptions;

/**
 * Checked exception indicating an error with a users request.
 * These should be intercepted by the interface and shown appropriately.
 *
 * @author Iain Price
 */
public abstract class UserException extends RuntimeException {
	private static final long serialVersionUID=1L;
	private boolean suppresslogging=false;

	public UserException(final String reason) { super(reason); }

	public UserException(final String reason,
	                     final Throwable cause) { super(reason,cause); }

	public UserException(final String reason,
	                     boolean suppresslogging) {
		super(reason);
		this.suppresslogging=suppresslogging;
	}

	public UserException(final String reason,
	                     final Throwable cause,
	                     boolean suppresslogging) {
		super(reason,cause);
		this.suppresslogging=suppresslogging;
	}
	
	public final boolean suppressed() { return suppresslogging; }
}
