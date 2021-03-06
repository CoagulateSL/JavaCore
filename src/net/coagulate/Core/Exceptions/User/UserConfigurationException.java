package net.coagulate.Core.Exceptions.User;

import net.coagulate.Core.Exceptions.UserException;

/**
 * The user configuration makes no sense
 */

public class UserConfigurationException extends UserException {
	private static final long serialVersionUID=1L;

	public UserConfigurationException(final String reason) {
		super(reason);
	}

	public UserConfigurationException(final String reason,
	                                  final Throwable cause) {
		super(reason,cause);
	}

	public UserConfigurationException(String reason, boolean suppresslogging) {
		super(reason, suppresslogging);
	}

	public UserConfigurationException(String reason, Throwable cause, boolean suppresslogging) {
		super(reason, cause, suppresslogging);
	}
}
