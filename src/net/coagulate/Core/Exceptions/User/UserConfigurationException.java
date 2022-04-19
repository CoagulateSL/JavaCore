package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

import java.io.Serial;

/**
 * The user configuration makes no sense
 */

public class UserConfigurationException extends UserException {
	@Serial
    private static final long serialVersionUID = 1L;

	public UserConfigurationException(final String message) {
		super(message);
	}

	public UserConfigurationException(final String message,
									  final Throwable cause) {
		super(message, cause);
	}

	public UserConfigurationException(final String reason, final boolean suppresslogging) {
		super(reason, suppresslogging);
	}

    public UserConfigurationException(final String reason, final Throwable cause, final boolean suppresslogging) {
        super(reason, cause, suppresslogging);
    }
}
