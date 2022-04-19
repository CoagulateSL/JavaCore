package net.coagulate.Core.Database;

import java.io.Serial;

public class DBUnexpectedNullValueException extends NoDataException {
    @Serial
    private static final long serialVersionUID = 1L;

    public DBUnexpectedNullValueException(final String message) {
        super(message);
    }

    public DBUnexpectedNullValueException(final String message,
                                          final Throwable cause) {
        super(message, cause);
    }
}
