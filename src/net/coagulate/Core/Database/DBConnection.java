package net.coagulate.Core.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTransactionRollbackException;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import net.coagulate.Core.Tools.UserException;

/**
 *
 * @author Iain Price
 */
public abstract class DBConnection {
    
    private static final boolean accumulatestats=true;
    private final Object querylock=new Object();
    private int queries=0;
    private long querytime=0;
    private long querymax=0;
    
    private final Object updatelock=new Object();
    private int updates=0;
    private long updatetime=0;
    private long updatemax=0;
  
    public class DBStats {
        public int queries;
        public long querytotal;
        public long querymax;
        public int updates;
        public long updatetotal;
        public long updatemax;
        public DBStats(int q,long qt,long qm,int u,long ut,long um) { queries=q; querytotal=qt; querymax=qm; updates=u; updatetotal=ut; updatemax=um; }
    }
    
    public DBStats getStats() {
        if (!accumulatestats) { throw new IllegalStateException("Stats are disabled"); }
        synchronized(querylock) {
            synchronized(updatelock) {
                DBStats stats=new DBStats(queries,querytime,querymax,updates,updatetime,updatemax);
                queries=0; querytime=0; querymax=0;
                updates=0; updatetime=0; updatemax=0;
                return stats;
            }
        }
    }
    
    static final boolean logsql=true; public static boolean sqlLogging() { return logsql; }
    
    Map<String,Integer> sqllog=new HashMap<>();
    Map<String,Long> sqllogsum=new HashMap<>();
    
    public void getSqlLogs(Map<String,Integer> count,Map<String,Long> runtime) throws UserException {
        if (!logsql) { throw new UserException("SQL Auditing is not enabled"); }
        count.putAll(sqllog); runtime.putAll(sqllogsum);
    }

    public abstract void shutdown();
    public abstract Connection getConnection();

    private final String sourcename;    
    protected DBConnection(String sourcename) {
        this.sourcename=sourcename;
        logger=Logger.getLogger(this.getClass().getName()+"."+sourcename);
    }
    public String getName() { return sourcename; }
    /** dont forget to call this during setup! */
    protected void register() { DB.register(sourcename,this); }

    Logger logger;
    
    public boolean test() {
        try {
            int result=dqi(true,"select count(*) from ping");
            if (result!=1) { throw new DBException("Select count(*) from ping returned not 1 ("+result+")"); }
            return true;
        }
        catch (Exception e){ logger.log(SEVERE,"Database connectivity test failure",e); }
        return false;
    }

     // some code I lifted from another project, and modified.  The "NullInteger" is a horrible hack, what am I doing :/
    // not the only one to come up with the typed-null class tho :P
    /** Internal database code that converts native types to SQL types.
     * 
     * @param conn DB connection 
     * @param parameterisedcommand SQL command
     * @param params SQL parameters
     * @return PreparedStatement as required
     * @throws DBException If there is an error.
     */
    protected PreparedStatement prepare(Connection conn,String parameterisedcommand,Object... params)
    {
        try {
            PreparedStatement ps=conn.prepareStatement(parameterisedcommand);
            for (int i=1;i<=params.length;i++)
            {
                    Object p=params[i-1];
                    boolean parsed=false;
                    if (p instanceof Integer)
                    {
                        { ps.setInt(i, (Integer)p); parsed=true; }
                    }
                    if (p instanceof String)
                    {
                        ps.setString(i, (String)p); parsed=true;
                    }
                    if (p instanceof NullInteger) {
                        ps.setNull(i,Types.INTEGER); parsed=true;
                    }
                    if (p instanceof Float)
                    {
                        ps.setFloat(i,(Float)p); parsed=true;
                    }
                    if (p instanceof Boolean) { ps.setBoolean(i,(Boolean)p); parsed=true; }
                    if (p instanceof Long) { ps.setLong(i, (Long)p); parsed=true; }
                    if (p==null) { ps.setNull(i,Types.VARCHAR); parsed=true; }
                    if (!parsed) { throw new DBException("Parameter "+i+" is not of a handled type ("+p.getClass().getName()+")"); }
            }
            return ps;
        }
        catch (SQLException e) { throw new DBException("Failed to prepare statement "+parameterisedcommand,e); }
    }
   
    
     // master query (dq - database query) - unpacks the resultset into a Results object containing Rows, and then closes everything out
    /** Call database with results.
     * Automatically handles resource allocation and release, unmarshalls results into a Results object (see that class).
     * These are not "views" on the database and do not support normal database view commands, this is a simple "get data from database" call.
     * @param parameterisedcommand SQL to query
     * @param params SQL parameters
     * @return Results (Set of Rows)
     */
    public Results dq(String parameterisedcommand,Object... params)
    {
        if (logsql) {
            if (sqllog.containsKey(parameterisedcommand)) {
                sqllog.put(parameterisedcommand,sqllog.get(parameterisedcommand)+1);
            } else {
                sqllog.put(parameterisedcommand,1);
            }
        }
        Connection conn=null; PreparedStatement stm=null; ResultSet rs=null;
        try {
            conn=getConnection();
            stm=prepare(conn,parameterisedcommand,params);
            long start=new Date().getTime();
            rs=stm.executeQuery();
            long end=new Date().getTime();
            long diff=end-start;
            if (DB.sqldebug_queries || (diff)>=DB.SLOWQUERYTHRESHOLD_QUERY) { logger.config("SQL ["+formatCaller()+"]:"+(diff)+"ms "+stm.toString()); }
            if (logsql) {
                if (sqllogsum.containsKey(parameterisedcommand)) {
                    sqllogsum.put(parameterisedcommand,sqllogsum.get(parameterisedcommand)+(diff));
                } else {
                    sqllogsum.put(parameterisedcommand,diff);
                }
            }
            if (accumulatestats) {
                synchronized(querylock) {
                    queries++;
                    querytime+=(diff);
                    if (querymax<diff) { querymax=diff; }
                }
            }
            Results results=new Results(rs);
            results.setStatement(stm.toString());
            return results;
        }
        catch (SQLException e) { throw new DBException("SQL Exception executing query "+parameterisedcommand,e); }
        finally {
            if (rs!=null) { try {rs.close(); } catch (SQLException e) {} }
            if (stm!=null) { try {stm.close();} catch (SQLException e) {}}
            if (conn!=null) { try { conn.close(); } catch (SQLException e) {}}
        }
    }   
    public String formatCaller() {
        String caller=formatFrame(5,"Unknown");
        String caller2=formatFrame(6,""); if (!caller2.isEmpty()) { caller+=", "+caller2; }
        return caller;
    }
    public String formatFrame(int framenumber,String def) {
        if (Thread.currentThread().getStackTrace().length>framenumber) {
            StackTraceElement element = Thread.currentThread().getStackTrace()[framenumber];
            String caller = element.getClassName()+"."+element.getMethodName();
            if (element.getLineNumber()>=0) { caller=caller+":"+element.getLineNumber(); }
            caller=caller.replaceAll("net.coagulate.","");
            return caller;
        }
        return def;
    }

    // database do, statement with no results
    /** Call database with no results.
     * Used for update / delete queries.]
     * Handles all resource allocation and teardown.
     * @param parameterisedcommand SQL query
     * @param params SQL arguments.
     */
    public void d(String parameterisedcommand,Object... params)
    {
        if (logsql) {
            if (sqllog.containsKey(parameterisedcommand)) {
                sqllog.put(parameterisedcommand,sqllog.get(parameterisedcommand)+1);
            } else {
                sqllog.put(parameterisedcommand,1);
            }
        }
        Connection conn=null; PreparedStatement stm=null;
        try {
            conn=getConnection();
            stm=prepare(conn,parameterisedcommand,params);
            long start=new Date().getTime();
            stm.execute();
            conn.commit();
            long end=new Date().getTime();
            long diff=end-start;
            if (DB.sqldebug_commands || (diff)>=DB.SLOWQUERYTHRESHOLD_UPDATE) { logger.finer("SQL "+(diff)+"ms ["+formatCaller()+"]:"+stm.toString()); }
            if (logsql) {
                if (sqllogsum.containsKey(parameterisedcommand)) {
                    sqllogsum.put(parameterisedcommand,sqllogsum.get(parameterisedcommand)+(diff));
                } else {
                    sqllogsum.put(parameterisedcommand,diff);
                }
            }
            if (accumulatestats) {
                synchronized(updatelock) {
                    updates++;
                    updatetime+=(diff);
                    if (updatemax<diff) { updatemax=diff; }
                }
            }
        }
        catch (SQLTransactionRollbackException e) {
            throw new LockException("Transaction conflicted and was rolled back",e);
        }
        catch (SQLException e) { throw new DBException("SQL error during command "+parameterisedcommand,e); }
        finally {
            if (stm!=null) { try {stm.close();} catch (SQLException e) {}}
            if (conn!=null) { try { conn.close(); } catch (SQLException e) {}}
        }

    }  
    
    /** for when we only expect one response, optionally exceptioning for zero results.
     * Mandatory is mainly intended to be used when you're /sure/ there should be a result, like you just made it, or referential integrity in the DB mandates it.
     * As such, were this to fail, throwing a SystemException is reasonable - the user will get a "Oops, we broke" and mailouts will happen.  This should not be routine.
     * If set to 'false' for mandatory, you get a null back, and you can handle this with your application level user error, if appropriate.  E.g. searching a username should return one, but the user might mess it up.
     * Note we always exception if we find more than one result, you should be using "dq()" directly if you expect 0 to many results, this method is only appropriate if you expect zero/one result, with "usually" one result.
     * @param mandatory Set true to return SQL exception on zero results, set false to return 'null'
     * @param parameterisedcommand SQL command
     * @param params SQL parameters
     * @return The singular row result, assuming exactly one row was found, or null if zero rows are found and mandatory is not set.
     */
    
    public ResultsRow dqone(boolean mandatory,String parameterisedcommand,Object... params) {
        Results results=dq(parameterisedcommand,params);
        if (results.size()==0) { 
            if (mandatory) {
                throw new NoDataException("Query "+results.getStatement()+" returned zero results and mandatory was set");
            } else {
                return null;
            }
        }
        if (results.size()>1) {
            throw new TooMuchDataException("Query "+results.getStatement()+" returned "+results.size()+" results and we expected one");
        }
        return results.iterator().next();
    }
    
    /** Convenience method for getting an integer.
     * Assumes a singular row result will be returned, containing a singular column, that we can convert to an integer.
     * The returned object is Integer NOT int, this is because Integers can be null.  This method may return a null Integer if and only if the mandatory flag is false, and zero results are found.
     * If you attempt to cast the null Integer to an int by auto(un)boxing, you'll throw runtime exceptions.
     * If you pass mandatory to be true, you should be /fine/ to auto assume it can convert to "int", because instead of a null result, a SytstemException would occur.
     * Please see dqone() for notes on the mandatory flag and exception behaviour policy in GPHUD.
     * Note null ints in the database are also returned as nulls, this will become ambiguous if you set mandatory to false.
     * @param mandatory If true, exceptions for zero results, otherwise returns "Null"
     * @param sql SQL to query
     * @param params Paramters to SQL
     * @return The integer form of the only column of the only row returned.  Can be null only if mandatory is false, and there are zero rows returned.  Or if the cell's contents are null.
     */
    public Integer dqi(boolean mandatory,String sql,Object... params) {
        ResultsRow row=dqone(mandatory,sql,params);
        if (row==null) { return null; }
        return row.getInt();
    }
    public Long dql(boolean mandatory,String sql,Object... params) {
        ResultsRow row=dqone(mandatory,sql,params);
        if (row==null) { return null; }
        return row.getLong();
    }

    /** Query for a string.
     * Returns the string from the first column of the only row returned.
     * Will systemexception (from called method) if multiple rows returned.
     * If no rows returned, will exception if mandatory is true, and return null if false.
     * Null values in the database will always be returned as nulls.  As such, mandatory=false masks two possibilities.
     * @param mandatory Exception if there are zero rows and mandatory is true.
     * @param sql SQL query
     * @param params SQL parameters
     * @return A String of the one column of the one row queried.  Null if the string in the DB is null.  Null if mandatory is false and zero rows are found.
     */
    public String dqs(boolean mandatory,String sql,Object... params) {
        ResultsRow row=dqone(mandatory,sql,params);
        if (row==null) { return null; }
        return row.getString();
    }
    
    
}
