package net.coagulate.Core.Database;

import java.io.Serial;

/**
 * Exception thrown when finding a row is mandatory, and none were found.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class NoDataException extends DBException {
	@Serial private static final long serialVersionUID=1L;
	
	public NoDataException(final String message) {
		super(message);
	}
	
	public NoDataException(final String message,final Throwable cause) {
		super(message,cause);
	}
}
