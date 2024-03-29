package net.coagulate.Core.Database;

import net.coagulate.Core.Exceptions.System.SystemImplementationException;
import net.coagulate.Core.Exceptions.User.UserConfigurationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.time.Instant;
import java.util.Date;
import java.util.*;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;

/**
 * @author Iain Price
 */
public abstract class DBConnection {
	
	static final         boolean             logSql         =true;
	private static final boolean             accumulateStats=true;
	private static final String              PACKAGE_PREFIX="net.coagulate.Core.Database";
	final                Map<String,Integer> sqlLog        =new HashMap<>();
	private              Instant             sqlLogSince   =Instant.now();
	final                Map<String,Long>    sqlLogSum      =new HashMap<>();
	final                Logger              logger;
	private final        Object              queryLock      =new Object();
	private final        Object              updateLock     =new Object();
	private final        String              sourceName;
	private final        Set<String> permittedCallers=new HashSet<>();
	private              int                 queries;
	private              long                queryTime;
	private              long                queryMax;
	private              int                 updates;
	private              long                updateTime;
	private              long                updateMax;
	
	public void resetSQLLogs() {
		sqlLog.clear();
		sqlLogSum.clear();
		sqlLogSince=Instant.now();
		Logger.getLogger("net.coagulate.Core.Database.DBConnection."+getName()).info("Reset SQL statistics on "+getName());
	}
	public void permit(final String prefix) {
		permittedCallers.add(prefix);
	}
	
	protected DBConnection(final String sourceName) {
		this.sourceName=sourceName;
		logger=Logger.getLogger(getClass().getName()+"."+sourceName);
	}
	
	// ---------- STATICS ----------
	public static boolean sqlLogging() {
		return logSql;
	}
	
	// ---------- INSTANCE ----------
	@Nonnull
	public DBStats getStats() {
		if (!accumulateStats) {
			throw new IllegalStateException("Stats are disabled");
		}
		synchronized(queryLock) {
			synchronized(updateLock) {
				final DBStats stats=new DBStats(queries,queryTime,queryMax,updates,updateTime,updateMax);
				queries=0;
				queryTime=0;
				queryMax=0;
				updates=0;
				updateTime=0;
				updateMax=0;
				return stats;
			}
		}
	}
	
	public Instant getSqlLogs(@Nonnull final Map<String,Integer> count,
	                       @Nonnull final Map<String,Long> runtime,
	                       @Nonnull final Map<String,Double> per) {
		if (!logSql) {
			throw new UserConfigurationException("SQL Auditing is not enabled");
		}
		count.putAll(sqlLog);
		runtime.putAll(sqlLogSum);
		for (final Map.Entry<String,Integer> entry: sqlLog.entrySet()) {
			final String statement=entry.getKey();
			final Integer c=entry.getValue();
			final Long runFor=runtime.get(statement);
			if (c==null||runFor==null||c==0) {
				per.put(statement,
				        0.0d); // explicitly a double.  even though it can be inferred, and is.  "wah wah this is an int not a double"...  it's both! :P
			} else {
				per.put(statement,((double)runFor)/((double)c)); /// :P
			}
		}
		return sqlLogSince;
	}
	
	public abstract void shutdown();
	
	@Nonnull
	public abstract Connection getConnection();
	
	/**
	 * Executes a slow query
	 *
	 * @param parameterisedCommand Parameterised SQL command
	 * @param params               SQL parameters
	 * @return Returns a results set, which may be empty
	 */
	@Nonnull
	public Results dqSlow(@Nonnull final String parameterisedCommand,final Object... params) {
		return _dq(true,parameterisedCommand,params);
	}
	
	public boolean test() {
		return true;
		/*try {
			final int result= dqiNotNull("select 1");
			if (result!=1) { throw new DBException("Select 1 returned not 1?? ("+result+")"); }
			return true;
		}
		catch (@Nonnull final Exception e) { logger.log(SEVERE,"Database connectivity test failure",e); }
		return false;*/
	}
	
	/**
	 * Executes a query
	 *
	 * @param slowQuery            Is this query known to be slow (don't warn about it)
	 * @param parameterisedCommand Parameterised SQL command
	 * @param params               SQL parameters
	 * @return Returns a results set, which may be empty
	 */
	@Nonnull
	private Results _dq(final boolean slowQuery,@Nonnull final String parameterisedCommand,final Object... params) {
		// permitted?
		permitCheck();
		if (logSql&&!slowQuery) {
			if (sqlLog.containsKey(parameterisedCommand)) {
				sqlLog.put(parameterisedCommand,sqlLog.get(parameterisedCommand)+1);
			} else {
				sqlLog.put(parameterisedCommand,1);
			}
		}
		Connection conn=null;
		PreparedStatement stm=null;
		ResultSet rs=null;
		try {
			conn=getConnection();
			stm=prepare(conn,parameterisedCommand,params);
			final long start=new Date().getTime();
			rs=stm.executeQuery();
			final long end=new Date().getTime();
			final long diff=end-start;
			// slow query mode turns off debug warnings about it being a slow query =)
			if (!slowQuery&&(DB.sqldebug_queries||(diff)>=DB.SLOWQUERYTHRESHOLD_QUERY)) {
				logger.config("SQL ["+formatCaller()+"]:"+(diff)+"ms "+stm);
			}
			if (logSql&&!slowQuery) {
				if (sqlLogSum.containsKey(parameterisedCommand)) {
					sqlLogSum.put(parameterisedCommand,sqlLogSum.get(parameterisedCommand)+(diff));
				} else {
					sqlLogSum.put(parameterisedCommand,diff);
				}
			}
			if (!slowQuery) {
				// these just horribly skew the average/sd etc... but they're still logged in the logSql above so smiley face
				if (accumulateStats) {
					synchronized(queryLock) {
						queries++;
						queryTime+=(diff);
						if (queryMax<diff) {
							queryMax=diff;
						}
					}
				}
			}
			final Results results=new Results(rs);
			results.setStatement(stm.toString());
			return results;
		} catch (@Nonnull final SQLException e) {
			throw new DBException("SQL Exception executing query "+parameterisedCommand,e);
		} finally {
			if (rs!=null) {
				try {
					rs.close();
				} catch (@Nonnull final SQLException ignored) {
				}
			}
			if (stm!=null) {
				try {
					stm.close();
				} catch (@Nonnull final SQLException ignored) {
				}
			}
			if (conn!=null) {
				try {
					conn.close();
				} catch (@Nonnull final SQLException ignored) {
				}
			}
		}
	}
	
	// check the caller's path is in the permitted list, if such a thing is set up
	private void permitCheck() {
		if (permittedCallers.isEmpty()) {
			return;
		} // fast path
		final StackTraceElement[] caller=Thread.currentThread().getStackTrace();
		for (int i=0;i<caller.length-1;i++) {
			final String className=caller[i].getClassName();
			if (!(className.startsWith(PACKAGE_PREFIX)||className.startsWith("java.lang"))) {
				for (final String permitted: permittedCallers) {
					if (className.startsWith(permitted)) {
						return;
					}
				}
				// uh oh
				//System.err.println("DB TRACING FAILURE");
				//System.err.println("Was tracing stack element "+className);
				//for (String permitted:permittedCallers) { System.err.println("Permitted:"+permitted); }
				Logger.getLogger("net.coagulate.Core.Database.DatabaseTracer")
				      .log(INFO,
				           "Unauthorised calling path to database '"+getName()+"' code",
				           new SystemImplementationException("Unauthorised database access from class "+className));
				return;
			}
		}
		System.err.println("FAILED CALLER TRACE:");
		for (int i=0;i<caller.length-1;i++) {
			System.err.println(caller[i].getClassName());
		}
		Logger.getLogger("net.coagulate.Core.Database.DatabaseTracer")
		      .log(WARNING,
		           "Failed to trace a caller properly?",
		           new SystemImplementationException("Failed to trace caller"));
	}
	
	// some code I lifted from another project, and modified.  The "NullInteger" is a horrible hack, what am I doing :/
	// not the only one to come up with the typed-null class tho :P
	
	/**
	 * Internal database code that converts native types to SQL types.
	 *
	 * @param conn                 DB connection
	 * @param parameterisedCommand SQL command
	 * @param params               SQL parameters
	 * @return PreparedStatement as required
	 *
	 * @throws DBException If there is an error.
	 */
	@Nonnull
	protected PreparedStatement prepare(@Nonnull final Connection conn,
	                                    final String parameterisedCommand,
	                                    @Nonnull final Object... params) {
		try {
			final PreparedStatement ps=conn.prepareStatement(parameterisedCommand);
			for (int i=1;i<=params.length;i++) {
				final Object p=params[i-1];
				boolean parsed=false;
				if (p instanceof Integer) {
					{
						ps.setInt(i,(Integer)p);
						parsed=true;
					}
				}
				if (p instanceof byte[]) {
					ps.setBytes(i,(byte[])p);
					parsed=true;
				}
				if (p instanceof final Byte[] in) {
					final byte[] out=new byte[in.length];
					for (int byteLoop=0;byteLoop<in.length;byteLoop++) {
						out[byteLoop]=in[byteLoop];
					}
					ps.setBytes(i,out);
					parsed=true;
				}
				if (p instanceof String) {
					ps.setString(i,(String)p);
					parsed=true;
				}
				if (p instanceof NullInteger) {
					ps.setNull(i,Types.INTEGER);
					parsed=true;
				}
				if (p instanceof Float) {
					ps.setFloat(i,(Float)p);
					parsed=true;
				}
				if (p instanceof Boolean) {
					ps.setBoolean(i,(Boolean)p);
					parsed=true;
				}
				if (p instanceof Long) {
					ps.setLong(i,(Long)p);
					parsed=true;
				}
				if (p==null) {
					ps.setNull(i,Types.VARCHAR);
					parsed=true;
				}
				if (!parsed) {
					throw new DBException("Parameter "+i+" is not of a handled type ("+p.getClass().getName()+")");
				}
			}
			return ps;
		} catch (@Nonnull final SQLException e) {
			throw new DBException("Failed to prepare statement "+parameterisedCommand,e);
		}
	}
	
	
	// master query (dq - database query) - unpacks the resultSet into a Results object containing Rows, and then closes everything out
	
	@Nonnull
	public String formatCaller() {
		String caller=formatFrame(5,"Unknown");
		final String caller2=formatFrame(6,"");
		if (!caller2.isEmpty()) {
			caller+=", "+caller2;
		}
		return caller;
	}
	
	@Nonnull
	public String getName() {
		return sourceName;
	}
	
	@Nonnull
	public String formatFrame(final int frameNumber,@Nonnull final String def) {
		if (Thread.currentThread().getStackTrace().length>frameNumber) {
			final StackTraceElement element=Thread.currentThread().getStackTrace()[frameNumber];
			String caller=element.getClassName()+"."+element.getMethodName();
			if (element.getLineNumber()>=0) {
				caller=caller+":"+element.getLineNumber();
			}
			caller=caller.replaceAll("net.coagulate.","");
			return caller;
		}
		return def;
	}
	
	/**
	 * Call database with no results, retries on locking exception.
	 * Used for update / delete queries.]
	 * Handles all resource allocation and tear down.
	 *
	 * @param parameterisedCommand SQL query
	 * @param params               SQL arguments.
	 */
	public void d(@Nonnull final String parameterisedCommand,final Object... params) {
		_d(false,parameterisedCommand,params);
	}

	private void _d(final boolean slowQuery,@Nonnull final String parameterisedCommand,final Object... params) {
		// permitted?
		permitCheck();
		if (logSql) {
			if (sqlLog.containsKey(parameterisedCommand)) {
				sqlLog.put(parameterisedCommand,sqlLog.get(parameterisedCommand)+1);
			} else {
				sqlLog.put(parameterisedCommand,1);
			}
		}
		try (final Connection conn=getConnection();
		     final PreparedStatement stm=prepare(conn,parameterisedCommand,params)) {
			final long start=new Date().getTime();
			stm.execute();
			final long end=new Date().getTime();
			final long diff=end-start;
			if (!slowQuery && (DB.sqldebug_commands||(diff)>=DB.SLOWQUERYTHRESHOLD_UPDATE)) {
				logger.finer("SQL "+(diff)+"ms ["+formatCaller()+"]:"+stm);
			}
			if (!slowQuery && logSql) {
				if (sqlLogSum.containsKey(parameterisedCommand)) {
					sqlLogSum.put(parameterisedCommand,sqlLogSum.get(parameterisedCommand)+(diff));
				} else {
					sqlLogSum.put(parameterisedCommand,diff);
				}
			}
			if (!slowQuery && accumulateStats) {
				synchronized(updateLock) {
					updates++;
					updateTime+=(diff);
					if (updateMax<diff) {
						updateMax=diff;
					}
				}
			}
		} catch (@Nonnull final SQLException e) {
			throw new DBException("SQL error during command "+parameterisedCommand,e);
		}
		
	}

	public void dSlow(@Nonnull final String parameterisedCommand,final Object... params) {
		_d(true,parameterisedCommand,params);
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
	public byte[] dqByteNotNull(@Nonnull final String sql,final Object... params) {
		final byte[] b=dqByte(sql,params);
		if (b==null) {
			throw new NoDataException("DB field unexpectedly contained null in "+sql);
		}
		return b;
	}
	
	// database do, statement with no results
	
	/**
	 * Convenience method for getting a byte array.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 * @return The byte array of the only column of the only row returned.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public byte[] dqByte(@Nonnull final String sql,final Object... params) {
		final ResultsRow row=dqOne(sql,params);
		return row.getBytes();
	}
	
	/**
	 * Query the database expecting one result.
	 *
	 * @param parameterisedCommand SQL command
	 * @param params               SQL parameters
	 * @return The singular row result, assuming exactly one row was found
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nonnull
	public ResultsRow dqOne(@Nonnull final String parameterisedCommand,final Object... params) {
		final Results results=dq(parameterisedCommand,params);
		if (results.size()==0) {
			throw new NoDataException("Query "+results.getStatement()+" returned zero results was set");
		}
		if (results.size()>1) {
			throw new TooMuchDataException(
					"Query "+results.getStatement()+" returned "+results.size()+" results and we expected one");
		}
		return results.first();
	}
	
	/**
	 * Call database with results.
	 * Automatically handles resource allocation and release, unmarshalls results into a Results object (see that class).
	 * These are not "views" on the database and do not support normal database view commands, this is a simple "get data from database" call.
	 *
	 * @param parameterisedCommand SQL to query
	 * @param params               SQL parameters
	 * @return Results (Set of Rows)
	 */
	@Nonnull
	public Results dq(@Nonnull final String parameterisedCommand,final Object... params) {
		return _dq(false,parameterisedCommand,params);
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
	public int dqiNotNull(@Nonnull final String sql,final Object... params) {
		final Integer i=dqi(sql,params);
		if (i==null) {
			throw new NoDataException("DB field unexpectedly contained null in "+sql);
		}
		return i;
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
	public Integer dqi(@Nonnull final String sql,final Object... params) {
		final ResultsRow row=dqOne(sql,params);
		return row.getIntNullable();
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
	public float dqfNotNull(@Nonnull final String sql,final Object... params) {
		final Float f=dqf(sql,params);
		if (f==null) {
			throw new NoDataException("DB field unexpectedly contained null in "+sql);
		}
		return f;
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
	public Float dqf(@Nonnull final String sql,final Object... params) {
		final ResultsRow row=dqOne(sql,params);
		return row.getFloatNullable();
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
	public long dqlNotNull(@Nonnull final String sql,final Object... params) {
		final Long l=dql(sql,params);
		if (l==null) {
			throw new NoDataException("DB field unexpectedly contained null in "+sql);
		}
		return l;
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
	public Long dql(@Nonnull final String sql,final Object... params) {
		final ResultsRow row=dqOne(sql,params);
		return row.getLongNullable();
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
	public String dqsNotNull(@Nonnull final String sql,final Object... params) {
		final String s=dqs(sql,params);
		if (s==null) {
			throw new NoDataException("DB field unexpectedly contained null in "+sql);
		}
		return s;
	}
	
	// ----- Internal Instance -----
	
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
	public String dqs(@Nonnull final String sql,final Object... params) {
		final ResultsRow row=dqOne(sql,params);
		return row.getStringNullable();
	}
	
	/**
	 * don't forget to call this during setup!
	 */
	protected void register() {
		DB.register(sourceName,this);
	}
	
	public static class DBStats {
		public final int  queries;
		public final long queryTotal;
		public final long queryMax;
		public final int  updates;
		public final long updateTotal;
		public final long updateMax;
		
		public DBStats(final int q,final long qt,final long qm,final int u,final long ut,final long um) {
			queries=q;
			queryTotal=qt;
			queryMax=qm;
			updates=u;
			updateTotal=ut;
			updateMax=um;
		}
	}
	
}
