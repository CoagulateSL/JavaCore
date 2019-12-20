package net.coagulate.Core.Database;

import net.coagulate.Core.Exceptions.SystemException;

/**
 * Represents errors with the GPHUD.getDB().
 * We've made this 'runtime' as theoretically /any/ sql statement can fail, so this can be thrown unchecked.
 * In most cases there's little remedy to this situation which means either:
 * 1) the database is down
 * 2) there's a programming error.
 * There's no real sane way to handle this so aborting the request and letting the main "handlers" handle an unchecked thrown exception is
 * probably about as good as we can do anyway.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class DBException extends SystemException {
	private static final long serialVersionUID=1L;

	public DBException(final String s) { super(s); }

	public DBException(final String s,
	                   final Throwable t) { super(s,t); }
}
