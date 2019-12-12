package net.coagulate.Core.Database;

/**
 * EXCLUSIVELY used to signal failure to acquire a lock.
 * <p>
 * Problems releasing a lock are coding errors and fall under SystemException
 *
 * @author Iain Price
 */
public class LockException extends DBException {
	private static final long serialVersionUID=1L;
	public LockException(String s, Throwable e) {
		super(s, e);
	}

	public LockException(String s) {
		super(s);
	}

}
