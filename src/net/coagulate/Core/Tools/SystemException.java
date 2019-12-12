package net.coagulate.Core.Tools;

/**
 * Internal errors in the code.
 * Unchecked, usually thrown to the top and logged.
 * Users are not shown these messages nor can they correct the issue.
 *
 * @author Iain Price
 */
public class SystemException extends RuntimeException {
	private static final long serialVersionUID=1L;
	public SystemException(String reason) { super(reason); }

	public SystemException(String reason, Throwable cause) { super(reason, cause); }
}
