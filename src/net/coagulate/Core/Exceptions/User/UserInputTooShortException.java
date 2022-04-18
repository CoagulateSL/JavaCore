package net.coagulate.Core.Exceptions.User;

/**
 * User input is too short
 */

public class UserInputTooShortException extends UserInputValidationException {
    private static final long serialVersionUID = 1L;

    public UserInputTooShortException(final String message) {
        super(message);
    }

    public UserInputTooShortException(final String message,
                                      final Throwable cause) {
        super(message, cause);
    }
}
