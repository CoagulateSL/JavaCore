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
	@Nonnull
	protected final PreparedStatement prepare(@Nonnull final Connection conn,
	                                          final String parameterisedcommand,
	                                          final Object... params) { return getDatabase().prepare(conn,parameterisedcommand,params); }

	@Nonnull
	public final Results dq(@Nonnull final String parameterisedcommand,
	                        final Object... params) { return getDatabase().dq(parameterisedcommand,params); }

	public final void d(@Nonnull final String parameterisedcommand,
	                    final Object... params) { getDatabase().d(parameterisedcommand,params); }

	@Nonnull
	public final ResultsRow dqone(@Nonnull final String parameterisedcommand,
	                              final Object... params) { return getDatabase().dqone(parameterisedcommand,params); }

	@Nullable
	public final Integer dqi(@Nonnull final String sql,
	                         final Object... params) { return getDatabase().dqi(sql,params); }

	@Nullable
	public final Float dqf(@Nonnull final String sql,
	                       final Object... params) { return getDatabase().dqf(sql,params); }

	@Nullable
	public final Long dql(@Nonnull final String sql,
	                      final Object... params) { return getDatabase().dql(sql,params); }

	@Nullable
	public final String dqs(@Nonnull final String sql,
	                        final Object... params) { return getDatabase().dqs(sql,params); }

	@Nullable
	public final byte[] dqbyte(@Nonnull final String sql,
	                           final Object... params) { return getDatabase().dqbyte(sql,params); }

	public final int dqinn(@Nonnull final String sql,
	                       final Object... params) { return getDatabase().dqinn(sql,params); }

	public final float dqfnn(@Nonnull final String sql,
	                         final Object... params) { return getDatabase().dqfnn(sql,params); }

	public final long dqlnn(@Nonnull final String sql,
	                        final Object... params) { return getDatabase().dqlnn(sql,params); }

	@Nonnull
	public final String dqsnn(@Nonnull final String sql,
	                          final Object... params) { return getDatabase().dqsnn(sql,params); }

	@Nonnull
	public final byte[] dqbytenn(@Nonnull final String sql,
	                             final Object... params) { return getDatabase().dqbytenn(sql,params); }

}
