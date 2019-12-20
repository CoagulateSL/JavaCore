package net.coagulate.Core.Database;

/**
 * Exception thrown when finding a row is mandatory, and none were found.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class NoDataException extends DBException {
	private static final long serialVersionUID=1L;

	public NoDataException(final String s) { super(s); }

	public NoDataException(final String e,
	                       final Throwable t) { super(e,t); }
}
