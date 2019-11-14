package net.coagulate.Core.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author Iain Price
 */
public abstract class Table {
	public abstract DBConnection getDatabase();

	public abstract String getTableName();

	// helpful methods
	protected final PreparedStatement prepare(Connection conn, String parameterisedcommand, Object... params) { return getDatabase().prepare(conn, parameterisedcommand, params); }

	public final Results dq(String parameterisedcommand, Object... params) { return getDatabase().dq(parameterisedcommand, params); }

	public final void d(String parameterisedcommand, Object... params) { getDatabase().d(parameterisedcommand, params); }

	public final ResultsRow dqone(boolean mandatory, String parameterisedcommand, Object... params) { return getDatabase().dqone(mandatory, parameterisedcommand, params); }

	public final Integer dqi(boolean mandatory, String sql, Object... params) { return getDatabase().dqi(mandatory, sql, params); }

	public final Float dqf(boolean mandatory, String sql, Object... params) { return getDatabase().dqf(mandatory, sql, params); }

	public final Long dql(boolean mandatory, String sql, Object... params) { return getDatabase().dql(mandatory, sql, params); }

	public final String dqs(boolean mandatory, String sql, Object... params) { return getDatabase().dqs(mandatory, sql, params); }

	public final byte[] dqbyte(boolean mandatory,String sql,Object... params) { return getDatabase().dqbyte(mandatory,sql,params); }
}
