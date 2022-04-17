package net.coagulate.Core.Exceptions.User;

public class UserConfigurationRecursionException extends UserConfigurationException {

    private static final long serialVersionUID = 1L;
    public UserConfigurationRecursionException(final String reason) {
        super(reason);
    }

    public UserConfigurationRecursionException(final String reason, final Throwable cause) {
        super(reason, cause);
    }
}
