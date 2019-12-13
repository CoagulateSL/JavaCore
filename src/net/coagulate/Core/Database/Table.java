package net.coagulate.Core.Database;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

	@Nullable
	public final Integer dqi(String sql, Object... params) { return getDatabase().dqi(sql, params); }

	@Nullable
	public final Float dqf(String sql, Object... params) { return getDatabase().dqf(sql, params); }

	@Nullable
	public final Long dql(String sql, Object... params) { return getDatabase().dql(sql, params); }

	@Nullable
	public final String dqs(String sql, Object... params) { return getDatabase().dqs(sql, params); }

	@Nullable
	public final byte[] dqbyte(String sql, Object... params) { return getDatabase().dqbyte(sql,params); }

	public final int dqinn(String sql, Object... params) { return getDatabase().dqinn(sql, params); }

	public final float dqfnn(String sql, Object... params) { return getDatabase().dqfnn(sql, params); }

	public final long dqlnn(String sql, Object... params) { return getDatabase().dqlnn(sql, params); }

	@Nonnull
	public final String dqsnn(String sql, Object... params) { return getDatabase().dqsnn(sql, params); }

	@Nonnull
	public final byte[] dqbytenn(String sql, Object... params) { return getDatabase().dqbytenn(sql,params); }

}
