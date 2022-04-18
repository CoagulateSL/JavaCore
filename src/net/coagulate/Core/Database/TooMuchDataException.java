package net.coagulate.Core.Database;

/**
 * Exception thrown when one row is expected but multiple are found.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class TooMuchDataException extends DBException {
    private static final long serialVersionUID = 1L;

    public TooMuchDataException(final String message) {
        super(message);
    }

    public TooMuchDataException(final String message,
                                final Throwable cause) {
        super(message, cause);
    }
}
