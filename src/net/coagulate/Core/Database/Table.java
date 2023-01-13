package net.coagulate.Core.Database;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * @author Iain Price
 */
public abstract class Table {
	// ---------- INSTANCE ----------
	@Nonnull
	public abstract DBConnection getDatabase();
	
	@Nonnull
	public abstract String getTableName();
	
	// helpful methods
	
	/**
	 * Call database with results.
	 * Automatically handles resource allocation and release, unmarshalls results into a Results object (see that class).
	 * These are not "views" on the database and do not support normal database view commands, this is a simple "get data from database" call.
	 *
	 * @param parameterisedcommand SQL to query
	 * @param params               SQL parameters
	 * @return Results (Set of Rows)
	 */
	@Nonnull
	public final Results dq(@Nonnull final String parameterisedcommand,final Object... params) {
		return getDatabase().dq(parameterisedcommand,params);
	}
	
	/**
	 * Call database with no results, retries on locking exception.
	 * Used for update / delete queries.]
	 * Handles all resource allocation and teardown.
	 *
	 * @param parameterisedcommand SQL query
	 * @param params               SQL arguments.
	 */
	public final void d(@Nonnull final String parameterisedcommand,final Object... params) {
		getDatabase().d(parameterisedcommand,params);
	}
	
	/**
	 * Query the database expecting one result.
	 *
	 * @param parameterisedcommand SQL command
	 * @param params               SQL parameters
	 * @return The singular row result, assuming exactly one row was found
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nonnull
	public final ResultsRow dqone(@Nonnull final String parameterisedcommand,final Object... params) {
		return getDatabase().dqOne(parameterisedcommand,params);
	}
	
	/**
	 * Convenience method for getting an integer.
	 * Assumes a singular row result will be returned, containing a singular column, that we can convert to an integer.
	 * If you attempt to cast the null Integer to an int by auto(un)boxing, you'll throw runtime exceptions.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 * @return The integer form of the only column of the only row returned.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public final Integer dqi(@Nonnull final String sql,final Object... params) {
		return getDatabase().dqi(sql,params);
	}
	
	/**
	 * Convenience method for getting a float.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 * @return The float form of the only column of the only row returned.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public final Float dqf(@Nonnull final String sql,final Object... params) {
		return getDatabase().dqf(sql,params);
	}
	
	/**
	 * Convenience method for getting a long.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 * @return The long form of the only column of the only row returned.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public final Long dql(@Nonnull final String sql,final Object... params) {
		return getDatabase().dql(sql,params);
	}
	
	/**
	 * Convenience method for getting a string.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 * @return The string form of the only column of the only row returned.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public final String dqs(@Nonnull final String sql,final Object... params) {
		return getDatabase().dqs(sql,params);
	}
	
	/**
	 * Convenience method for getting an integer.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 * @return The integer form of the only column of the only row returned.  Can not be null.
	 *
	 * @throws NoDataException      if there are no results OR the value in the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	public final int dqinn(@Nonnull final String sql,final Object... params) {
		return getDatabase().dqiNotNull(sql,params);
	}
	
	/**
	 * Convenience method for getting a float.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 * @return The float form of the only column of the only row returned.  Can not be null.
	 *
	 * @throws NoDataException      if there are no results OR the value in the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	public final float dqfnn(@Nonnull final String sql,final Object... params) {
		return getDatabase().dqfNotNull(sql,params);
	}
	
	/**
	 * Convenience method for getting a long.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 * @return The long form of the only column of the only row returned.  Can not be null.
	 *
	 * @throws NoDataException      if there are no results OR the value in the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	public final long dqlnn(@Nonnull final String sql,final Object... params) {
		return getDatabase().dqlNotNull(sql,params);
	}
	
	/**
	 * Convenience method for getting a string.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 * @return The string form of the only column of the only row returned.  Can not be null.
	 *
	 * @throws NoDataException      if there are no results OR the value in the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nonnull
	public final String dqsnn(@Nonnull final String sql,final Object... params) {
		return getDatabase().dqsNotNull(sql,params);
	}
	
	/**
	 * Convenience method for getting a byte array.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 * @return The byte array form of the only column of the only row returned.  Can not be null.
	 *
	 * @throws NoDataException      if there are no results OR the value in the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nonnull
	public final byte[] dqbytenn(@Nonnull final String sql,final Object... params) {
		return getDatabase().dqByteNotNull(sql,params);
	}
	
	// ----- Internal Instance -----
	
	/**
	 * Internal database code that converts native types to SQL types.
	 *
	 * @param conn                 DB connection
	 * @param parameterisedcommand SQL command
	 * @param params               SQL parameters
	 * @return PreparedStatement as required
	 *
	 * @throws DBException If there is an error.
	 */
	@Nonnull
	protected final PreparedStatement prepare(@Nonnull final Connection conn,
	                                          final String parameterisedcommand,
	                                          final Object... params) {
		return getDatabase().prepare(conn,parameterisedcommand,params);
	}
	
}
