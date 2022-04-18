package net.coagulate.Core.Database;

public class DBUnexpectedNullValueException extends NoDataException {
    private static final long serialVersionUID = 1L;

    public DBUnexpectedNullValueException(final String message) {
        super(message);
    }

    public DBUnexpectedNullValueException(final String message,
                                          final Throwable cause) {
        super(message, cause);
    }
}
