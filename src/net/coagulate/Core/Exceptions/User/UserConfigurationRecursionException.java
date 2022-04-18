package net.coagulate.Core.Exceptions.User;

public class UserConfigurationRecursionException extends UserConfigurationException {

    private static final long serialVersionUID = 1L;

    public UserConfigurationRecursionException(final String message) {
        super(message);
    }

    public UserConfigurationRecursionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
