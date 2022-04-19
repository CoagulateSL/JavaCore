package net.coagulate.Core.Exceptions.User;

import java.io.Serial;

public class UserConfigurationRecursionException extends UserConfigurationException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserConfigurationRecursionException(final String message) {
        super(message);
    }

    public UserConfigurationRecursionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
