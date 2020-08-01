package net.coagulate.Core.Database;

import net.coagulate.Core.Exceptions.System.SystemImplementationException;
import net.coagulate.Core.Exceptions.User.UserConfigurationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

import static java.util.logging.Level.*;

/**
 * @author Iain Price
 */
public abstract class DBConnection {

	static final boolean logsql=true;
	private static final boolean accumulatestats=true;
	final Map<String,Integer> sqllog=new HashMap<>();
	final Map<String,Long> sqllogsum=new HashMap<>();
	final Logger logger;
	private final Object querylock=new Object();
	private final Object updatelock=new Object();
	private final String sourcename;
	private int queries;
	private long querytime;
	private long querymax;
	private int updates;
	private long updatetime;
	private long updatemax;

	private static final String PACKAGEPREFIX="net.coagulate.Core.Database";
	private final Set<String> permittedcallers=new HashSet<>();
	public void permit(String prefix) { permittedcallers.add(prefix); }

	protected DBConnection(final String sourcename) {
		this.sourcename=sourcename;
		logger=Logger.getLogger(getClass().getName()+"."+sourcename);
	}

	// ---------- STATICS ----------
	public static boolean sqlLogging() { return logsql; }

	// ---------- INSTANCE ----------
	@Nonnull
	public DBStats getStats() {
		if (!accumulatestats) { throw new IllegalStateException("Stats are disabled"); }
		synchronized (querylock) {
			synchronized (updatelock) {
				final DBStats stats=new DBStats(queries,querytime,querymax,updates,updatetime,updatemax);
				queries=0;
				querytime=0;
				querymax=0;
				updates=0;
				updatetime=0;
				updatemax=0;
				return stats;
			}
		}
	}

	public void getSqlLogs(@Nonnull final Map<String,Integer> count,
	                       @Nonnull final Map<String,Long> runtime,
	                       @Nonnull final Map<String,Double> per) {
		if (!logsql) { throw new UserConfigurationException("SQL Auditing is not enabled"); }
		count.putAll(sqllog);
		runtime.putAll(sqllogsum);
		for (final Map.Entry<String,Integer> entry: sqllog.entrySet()) {
			final String statement=entry.getKey();
			final Integer c=entry.getValue();
			final Long runfor=runtime.get(statement);
			if (c==null || runfor==null || c==0) {
				per.put(statement,0d); // explicitly a double.  even though it can be inferred, and is.  "wah wah this is an int not a double"...  it's both! :P
			}
			else {
				per.put(statement,((double) runfor)/((double) c)); /// :P
			}
		}
	}

	public abstract void shutdown();

	@Nonnull
	public abstract Connection getConnection();

	@Nonnull
	public String getName() { return sourcename; }

	public boolean test() {
		try {
			final int result=dqinn("select 1");
			if (result!=1) { throw new DBException("Select 1 returned not 1?? ("+result+")"); }
			return true;
		}
		catch (@Nonnull final Exception e) { logger.log(SEVERE,"Database connectivity test failure",e); }
		return false;
	}

	/**
	 * Call database with results.
	 * Automatically handles resource allocation and release, unmarshalls results into a Results object (see that class).
	 * These are not "views" on the database and do not support normal database view commands, this is a simple "get data from database" call.
	 *
	 * @param parameterisedcommand SQL to query
	 * @param params               SQL parameters
	 *
	 * @return Results (Set of Rows)
	 */
	@Nonnull
	public Results dq(@Nonnull final String parameterisedcommand,
	                  final Object... params) {
		return _dq(false,parameterisedcommand,params);
	}

	/**
	 * Executes a slow query
	 *
	 * @param parameterisedcommand Parameterised SQL command
	 * @param params               SQL parameters
	 *
	 * @return Returns a results set, which may be empty
	 */
	@Nonnull
	public Results dqSlow(@Nonnull final String parameterisedcommand,
	                      final Object... params) {
		return _dq(true,parameterisedcommand,params);
	}

	// some code I lifted from another project, and modified.  The "NullInteger" is a horrible hack, what am I doing :/
	// not the only one to come up with the typed-null class tho :P

	/**
	 * Executes a query
	 *
	 * @param slowquery            Is this query known to be slow (don't warn about it)
	 * @param parameterisedcommand Parameterised SQL command
	 * @param params               SQL parameters
	 *
	 * @return Returns a results set, which may be empty
	 */
	@Nonnull
	public Results _dq(final boolean slowquery,
	                   @Nonnull final String parameterisedcommand,
	                   final Object... params) {
		// permitted?
		permitCheck();
		if (logsql) {
			if (sqllog.containsKey(parameterisedcommand)) {
				sqllog.put(parameterisedcommand,sqllog.get(parameterisedcommand)+1);
			}
			else {
				sqllog.put(parameterisedcommand,1);
			}
		}
		Connection conn=null;
		PreparedStatement stm=null;
		ResultSet rs=null;
		try {
			conn=getConnection();
			stm=prepare(conn,parameterisedcommand,params);
			final long start=new Date().getTime();
			rs=stm.executeQuery();
			final long end=new Date().getTime();
			final long diff=end-start;
			// slow query mode turns off debug warnings about it being a slow query =)
			if (!slowquery && (DB.sqldebug_queries || (diff) >= DB.SLOWQUERYTHRESHOLD_QUERY)) {
				logger.config("SQL ["+formatCaller()+"]:"+(diff)+"ms "+stm);
			}
			if (logsql) {
				if (sqllogsum.containsKey(parameterisedcommand)) {
					sqllogsum.put(parameterisedcommand,sqllogsum.get(parameterisedcommand)+(diff));
				}
				else {
					sqllogsum.put(parameterisedcommand,diff);
				}
			}
			if (!slowquery) {
				// these just horribly skew the average/sd etc... but they're still logged in the logsql above so smiley face
				if (accumulatestats) {
					synchronized (querylock) {
						queries++;
						querytime+=(diff);
						if (querymax<diff) { querymax=diff; }
					}
				}
			}
			final Results results=new Results(rs);
			results.setStatement(stm.toString());
			return results;
		}
		catch (@Nonnull final SQLException e) {
			throw new DBException("SQL Exception executing query "+parameterisedcommand,e);
		}
		finally {
			if (rs!=null) {
				try {rs.close(); } catch (@Nonnull final SQLException ignored) {}
			}
			if (stm!=null) {
				try {stm.close();} catch (@Nonnull final SQLException ignored) {}
			}
			if (conn!=null) {
				try { conn.close(); } catch (@Nonnull final SQLException ignored) {}
			}
		}
	}


	// master query (dq - database query) - unpacks the resultset into a Results object containing Rows, and then closes everything out

	@Nonnull
	public String formatCaller() {
		String caller=formatFrame(5,"Unknown");
		final String caller2=formatFrame(6,"");
		if (!caller2.isEmpty()) { caller+=", "+caller2; }
		return caller;
	}

	@Nonnull
	public String formatFrame(final int framenumber,
	                          @Nonnull final String def) {
		if (Thread.currentThread().getStackTrace().length>framenumber) {
			final StackTraceElement element=Thread.currentThread().getStackTrace()[framenumber];
			String caller=element.getClassName()+"."+element.getMethodName();
			if (element.getLineNumber() >= 0) { caller=caller+":"+element.getLineNumber(); }
			caller=caller.replaceAll("net.coagulate.","");
			return caller;
		}
		return def;
	}

	/**
	 * Call database with no results, retries on locking exception.
	 * Used for update / delete queries.]
	 * Handles all resource allocation and teardown.
	 *
	 * @param parameterisedcommand SQL query
	 * @param params               SQL arguments.
	 */
	public void d(@Nonnull final String parameterisedcommand,
	              final Object... params) {
		// permitted?
		permitCheck();
		if (logsql) {
			if (sqllog.containsKey(parameterisedcommand)) {
				sqllog.put(parameterisedcommand,sqllog.get(parameterisedcommand)+1);
			}
			else {
				sqllog.put(parameterisedcommand,1);
			}
		}
		try (final Connection conn=getConnection();final PreparedStatement stm=prepare(conn,parameterisedcommand,params)) {
			final long start=new Date().getTime();
			stm.execute();
			conn.commit();
			final long end=new Date().getTime();
			final long diff=end-start;
			if (DB.sqldebug_commands || (diff) >= DB.SLOWQUERYTHRESHOLD_UPDATE) {
				logger.finer("SQL "+(diff)+"ms ["+formatCaller()+"]:"+stm);
			}
			if (logsql) {
				if (sqllogsum.containsKey(parameterisedcommand)) {
					sqllogsum.put(parameterisedcommand,sqllogsum.get(parameterisedcommand)+(diff));
				}
				else {
					sqllogsum.put(parameterisedcommand,diff);
				}
			}
			if (accumulatestats) {
				synchronized (updatelock) {
					updates++;
					updatetime+=(diff);
					if (updatemax<diff) { updatemax=diff; }
				}
			}
		}
		catch (@Nonnull final SQLException e) {
			throw new DBException("SQL error during command "+parameterisedcommand,e);
		}

	}

	// check the caller's path is in the permitted list, if such a thing is set up
	private void permitCheck() {
		if (permittedcallers.isEmpty()) { return; } // fast path
		StackTraceElement[] caller = Thread.currentThread().getStackTrace();
		for (int i=0;i<caller.length-1;i++) {
			String classname=caller[i].getClassName();
			if (!(classname.startsWith(PACKAGEPREFIX) || classname.startsWith("java.lang"))) {
				for (String permitted:permittedcallers) {
					if (classname.startsWith(permitted)) { return; }
				}
				// uhoh
				Logger.getLogger("net.coagulate.Core.Database.DatabaseTracer").log(INFO,"Unauthorised calling path to database code",new SystemImplementationException("Unauthorised database access from class "+classname));
				return;
			}
		}
		System.err.println("FAILED CALLER TRACE:");
		for (int i=0;i<caller.length-1;i++) { System.err.println(caller[i].getClassName()); }
		Logger.getLogger("net.coagulate.Core.Database.DatabaseTracer").log(WARNING,"Failed to trace a caller properly?",new SystemImplementationException("Failed to trace caller"));
	}

	/**
	 * Query the database expecting one result.
	 *
	 * @param parameterisedcommand SQL command
	 * @param params               SQL parameters
	 *
	 * @return The singular row result, assuming exactly one row was found
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nonnull
	public ResultsRow dqone(@Nonnull final String parameterisedcommand,
	                        final Object... params) {
		final Results results=dq(parameterisedcommand,params);
		if (results.size()==0) {
			throw new NoDataException("Query "+results.getStatement()+" returned zero results was set");
		}
		if (results.size()>1) {
			throw new TooMuchDataException("Query "+results.getStatement()+" returned "+results.size()+" results and we expected one");
		}
		return results.first();
	}

	// database do, statement with no results

	/**
	 * Convenience method for getting an integer.
	 * Assumes a singular row result will be returned, containing a singular column, that we can convert to an integer.
	 * If you attempt to cast the null Integer to an int by auto(un)boxing, you'll throw runtime exceptions.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 *
	 * @return The integer form of the only column of the only row returned.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public Integer dqi(@Nonnull final String sql,
	                   final Object... params) {
		final ResultsRow row=dqone(sql,params);
		return row.getIntNullable();
	}

	/**
	 * Convenience method for getting a float.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 *
	 * @return The float form of the only column of the only row returned.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public Float dqf(@Nonnull final String sql,
	                 final Object... params) {
		final ResultsRow row=dqone(sql,params);
		return row.getFloatNullable();
	}

	/**
	 * Convenience method for getting a long.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 *
	 * @return The long form of the only column of the only row returned.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public Long dql(@Nonnull final String sql,
	                final Object... params) {
		final ResultsRow row=dqone(sql,params);
		return row.getLongNullable();
	}

	/**
	 * Convenience method for getting a string.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 *
	 * @return The string form of the only column of the only row returned.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public String dqs(@Nonnull final String sql,
	                  final Object... params) {
		final ResultsRow row=dqone(sql,params);
		return row.getStringNullable();
	}

	/**
	 * Convenience method for getting a byte array.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 *
	 * @return The byte array of the only column of the only row returned.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public byte[] dqbyte(@Nonnull final String sql,
	                     final Object... params) {
		final ResultsRow row=dqone(sql,params);
		return row.getBytes();
	}

	/**
	 * Convenience method for getting a byte array.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 *
	 * @return The byte array form of the only column of the only row returned.  Can not be null.
	 *
	 * @throws NoDataException      if there are no results OR the value in the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	public byte[] dqbytenn(@Nonnull final String sql,
	                       final Object... params) {
		final byte[] b=dqbyte(sql,params);
		if (b==null) { throw new NoDataException("DB field unexpectedly contained null in "+sql); }
		return b;
	}

	/**
	 * Convenience method for getting an integer.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 *
	 * @return The integer form of the only column of the only row returned.  Can not be null.
	 *
	 * @throws NoDataException      if there are no results OR the value in the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	public int dqinn(@Nonnull final String sql,
	                 final Object... params) {
		final Integer i=dqi(sql,params);
		if (i==null) { throw new NoDataException("DB field unexpectedly contained null in "+sql); }
		return i;
	}

	/**
	 * Convenience method for getting a float.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 *
	 * @return The float form of the only column of the only row returned.  Can not be null.
	 *
	 * @throws NoDataException      if there are no results OR the value in the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	public float dqfnn(@Nonnull final String sql,
	                   final Object... params) {
		final Float f=dqf(sql,params);
		if (f==null) { throw new NoDataException("DB field unexpectedly contained null in "+sql); }
		return f;
	}

	/**
	 * Convenience method for getting a long.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 *
	 * @return The long form of the only column of the only row returned.  Can not be null.
	 *
	 * @throws NoDataException      if there are no results OR the value in the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	public long dqlnn(@Nonnull final String sql,
	                  final Object... params) {
		final Long l=dql(sql,params);
		if (l==null) { throw new NoDataException("DB field unexpectedly contained null in "+sql); }
		return l;
	}

	/**
	 * Convenience method for getting a string.
	 *
	 * @param sql    SQL to query
	 * @param params Parameters to SQL
	 *
	 * @return The string form of the only column of the only row returned.  Can not be null.
	 *
	 * @throws NoDataException      if there are no results OR the value in the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nonnull
	public String dqsnn(@Nonnull final String sql,
	                    final Object... params) {
		final String s=dqs(sql,params);
		if (s==null) { throw new NoDataException("DB field unexpectedly contained null in "+sql); }
		return s;
	}

	// ----- Internal Instance -----

	/**
	 * don't forget to call this during setup!
	 */
	protected void register() { DB.register(sourcename,this); }

	/**
	 * Internal database code that converts native types to SQL types.
	 *
	 * @param conn                 DB connection
	 * @param parameterisedcommand SQL command
	 * @param params               SQL parameters
	 *
	 * @return PreparedStatement as required
	 *
	 * @throws DBException If there is an error.
	 */
	@Nonnull
	protected PreparedStatement prepare(@Nonnull final Connection conn,
	                                    final String parameterisedcommand,
	                                    @Nonnull final Object... params) {
		try {
			final PreparedStatement ps=conn.prepareStatement(parameterisedcommand);
			for (int i=1;i<=params.length;i++) {
				final Object p=params[i-1];
				boolean parsed=false;
				if (p instanceof Integer) {
					{
						ps.setInt(i,(Integer) p);
						parsed=true;
					}
				}
				if (p instanceof byte[]) {
					ps.setBytes(i,(byte[]) p);
					parsed=true;
				}
				if (p instanceof Byte[]) {
					final Byte[] in=(Byte[]) p;
					final byte[] out=new byte[in.length];
					for (int byteloop=0;byteloop<in.length;byteloop++) { out[byteloop]=in[byteloop]; }
					ps.setBytes(i,out);
					parsed=true;
				}
				if (p instanceof String) {
					ps.setString(i,(String) p);
					parsed=true;
				}
				if (p instanceof NullInteger) {
					ps.setNull(i,Types.INTEGER);
					parsed=true;
				}
				if (p instanceof Float) {
					ps.setFloat(i,(Float) p);
					parsed=true;
				}
				if (p instanceof Boolean) {
					ps.setBoolean(i,(Boolean) p);
					parsed=true;
				}
				if (p instanceof Long) {
					ps.setLong(i,(Long) p);
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
		}
		catch (@Nonnull final SQLException e) {
			throw new DBException("Failed to prepare statement "+parameterisedcommand,e);
		}
	}

	public static class DBStats {
		public final int queries;
		public final long querytotal;
		public final long querymax;
		public final int updates;
		public final long updatetotal;
		public final long updatemax;

		public DBStats(final int q,
		               final long qt,
		               final long qm,
		               final int u,
		               final long ut,
		               final long um) {
			queries=q;
			querytotal=qt;
			querymax=qm;
			updates=u;
			updatetotal=ut;
			updatemax=um;
		}
	}

}
