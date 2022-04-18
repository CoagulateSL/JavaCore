package net.coagulate.Core.Exceptions.User;

/**
 * User input is too short
 */

public class UserInputInvalidChoiceException extends UserInputValidationException {
    private static final long serialVersionUID = 1L;

    public UserInputInvalidChoiceException(final String message) {
        super(message);
    }

    public UserInputInvalidChoiceException(final String message,
                                           final Throwable cause) {
        super(message, cause);
    }

    public UserInputInvalidChoiceException(final String reason, final Throwable cause, final boolean suppresslogging) {
        super(reason, cause, suppresslogging);
    }

    public UserInputInvalidChoiceException(final String reason, final boolean suppresslogging) {
        super(reason, suppresslogging);
    }
}
