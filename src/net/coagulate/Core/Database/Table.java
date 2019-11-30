package net.coagulate.Core.Database;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author Iain Price
 */
public abstract class Table {
	@Nullable
	public abstract DBConnection getDatabase();

	@Nonnull
	public abstract String getTableName();

	// helpful methods
	protected final PreparedStatement prepare(@Nonnull Connection conn, String parameterisedcommand, Object... params) { return getDatabase().prepare(conn, parameterisedcommand, params); }

	@Nullable
	public final Results dq(String parameterisedcommand, Object... params) { return getDatabase().dq(parameterisedcommand, params); }

	public final void d(String parameterisedcommand, Object... params) { getDatabase().d(parameterisedcommand, params); }

	@Nullable
	public final ResultsRow dqone(boolean mandatory, String parameterisedcommand, Object... params) { return getDatabase().dqone(mandatory, parameterisedcommand, params); }

	@Nullable
	public final Integer dqi(boolean mandatory, String sql, Object... params) { return getDatabase().dqi(mandatory, sql, params); }

	@Nullable
	public final Float dqf(boolean mandatory, String sql, Object... params) { return getDatabase().dqf(mandatory, sql, params); }

	@Nullable
	public final Long dql(boolean mandatory, String sql, Object... params) { return getDatabase().dql(mandatory, sql, params); }

	@Nullable
	public final String dqs(boolean mandatory, String sql, Object... params) { return getDatabase().dqs(mandatory, sql, params); }

	@Nullable
	public final byte[] dqbyte(boolean mandatory, String sql, Object... params) { return getDatabase().dqbyte(mandatory,sql,params); }
}
