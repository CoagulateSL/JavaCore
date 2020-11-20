package net.coagulate.Core.Exceptions.User;

public class UserConfigurationRecursionException extends UserConfigurationException {
    public UserConfigurationRecursionException(String reason) {
        super(reason);
    }

    public UserConfigurationRecursionException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
