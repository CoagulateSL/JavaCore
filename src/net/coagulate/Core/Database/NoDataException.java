package net.coagulate.Core.Database;

/**
 * Exception thrown when finding a row is mandatory, and none were found.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class NoDataException extends DBException {
	private static final long serialVersionUID=1L;
	public NoDataException(String s) { super(s); }

	public NoDataException(String e, Throwable t) { super(e, t); }
}
