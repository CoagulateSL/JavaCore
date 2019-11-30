package net.coagulate.Core.Database;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author Iain Price
 */
public abstract class Table {
	@Nonnull
	public abstract DBConnection getDatabase();

	@Nonnull
	public abstract String getTableName();

	// helpful methods
	protected final PreparedStatement prepare(@Nonnull Connection conn, String parameterisedcommand, Object... params) { return getDatabase().prepare(conn, parameterisedcommand, params); }

	@Nonnull
	public final Results dq(String parameterisedcommand, Object... params) { return getDatabase().dq(parameterisedcommand, params); }

	public final void d(String parameterisedcommand, Object... params) { getDatabase().d(parameterisedcommand, params); }

	@Nonnull
	public final ResultsRow dqone(String parameterisedcommand, Object... params) { return getDatabase().dqone(parameterisedcommand, params); }

	@Nonnull
	public final Integer dqi(String sql, Object... params) { return getDatabase().dqi(sql, params); }

	@Nonnull
	public final Float dqf(String sql, Object... params) { return getDatabase().dqf(sql, params); }

	@Nonnull
	public final Long dql(String sql, Object... params) { return getDatabase().dql(sql, params); }

	@Nonnull
	public final String dqs(String sql, Object... params) { return getDatabase().dqs(sql, params); }

	@Nonnull
	public final byte[] dqbyte(String sql, Object... params) { return getDatabase().dqbyte(sql,params); }
}
