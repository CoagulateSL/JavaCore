package net.coagulate.Core.Exceptions;

/**
 * Internal errors in the code.
 * Unchecked, usually thrown to the top and logged.
 * Users are not shown these messages nor can they correct the issue.
 *
 * @author Iain Price
 */
public abstract class SystemException extends RuntimeException {
	private static final long serialVersionUID=1L;
	public SystemException(final String reason) { super(reason); }

	public SystemException(final String reason, final Throwable cause) { super(reason, cause); }
}
