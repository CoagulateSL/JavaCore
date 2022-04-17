package net.coagulate.Core.Exceptions.User;

public class UserConfigurationRecursionException extends UserConfigurationException {

    private static final long serialVersionUID = 1L;
    public UserConfigurationRecursionException(String reason) {
        super(reason);
    }

    public UserConfigurationRecursionException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
