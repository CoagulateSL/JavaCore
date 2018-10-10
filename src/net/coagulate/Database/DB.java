package net.coagulate.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Random;
import net.coagulate.Core.UnixTime;
import net.coagulate.SL.Config;
import net.coagulate.SL.DBException;
import net.coagulate.SL.Database.*;
import net.coagulate.SL.LockException;
import net.coagulate.SL.SL;
import net.coagulate.SL.SystemException;

/** Handles the database connection
 * Uses a database pool to create connections.
 * Takes incoming parameterised SQL queries for preparedStatement
 * Unmarshals results into hashmap, array of arrays, single value, whatever, lots of tedious DB support methods, "getInt(sql)" etc
 * Releases result-sets etc once unmarshalled.
 * 
 * NOTE: The database is asynchronously replicating, data should not be cached and used for updates for any extensive period of time.
 * be aware other threads may have modified the data in the mean time, ensure you accomodate or override the values as appropriate.
 * 
 * @author Iain Price <gphud@predestined.net>
 */
public abstract class DB {
    private static final long SLOWQUERYTHRESHOLD_QUERY=100;
    private static final long SLOWQUERYTHRESHOLD_UPDATE=100;
    private static final boolean sqldebug_queries=false;
    private static final boolean sqldebug_commands=false;
    
    

    

    
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
    private static PreparedStatement prepare(Connection conn,String parameterisedcommand,Object... params)
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
                    if (p instanceof Boolean) { ps.setBoolean(i,(Boolean)p); parsed=true; }
                    if (p instanceof Long) { ps.setLong(i, (Long)p); parsed=true; }
                    if (p==null) { ps.setNull(i,Types.VARCHAR); parsed=true; }
                    if (!parsed) { throw new DBException("Parameter "+i+" is not of a handled type ("+p.getClass().getName()+")"); }
            }
            return ps;
        }
        catch (SQLException e) { throw new DBException("Failed to prepare statement "+parameterisedcommand,e); }
    }
    // database do, statement with no results
    /** Call database with no results.
     * Used for update / delete queries.]
     * Handles all resource allocation and teardown.
     * @param parameterisedcommand SQL query
     * @param params SQL arguments.
     */
    public static void d(String parameterisedcommand,Object... params)
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
            if (sqldebug_commands || (end-start)>=SLOWQUERYTHRESHOLD_UPDATE) { SL.getLogger().finer("SQL:"+(end-start)+"ms "+stm.toString()); }
            if (logsql) {
                if (sqllogsum.containsKey(parameterisedcommand)) {
                    sqllogsum.put(parameterisedcommand,sqllogsum.get(parameterisedcommand)+(end-start));
                } else {
                    sqllogsum.put(parameterisedcommand,end-start);
                }
            }               
        }
        catch (SQLException e) { throw new DBException("SQL error during command "+parameterisedcommand,e); }
        finally {
            if (stm!=null) { try {stm.close();} catch (SQLException e) {}}
            if (conn!=null) { try { conn.close(); } catch (SQLException e) {}}
        }

    }
    // master query (dq - database query) - unpacks the resultset into a Results object containing Rows, and then closes everything out
    /** Call database with results.
     * Automatically handles resource allocation and release, unmarshalls results into a Results object (see that class).
     * These are not "views" on the database and do not support normal database view commands, this is a simple "get data from database" call.
     * @param parameterisedcommand SQL to query
     * @param params SQL parameters
     * @return Results (Set of Rows)
     */
    public static Results dq(String parameterisedcommand,Object... params)
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
            if (sqldebug_queries || (end-start)>=SLOWQUERYTHRESHOLD_QUERY) { SL.getLogger().config("SQL:"+(end-start)+"ms "+stm.toString()); }
            if (logsql) {
                if (sqllogsum.containsKey(parameterisedcommand)) {
                    sqllogsum.put(parameterisedcommand,sqllogsum.get(parameterisedcommand)+(end-start));
                } else {
                    sqllogsum.put(parameterisedcommand,end-start);
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
    
    public static Row dqone(boolean mandatory,String parameterisedcommand,Object... params) {
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
    public static Integer dqi(boolean mandatory,String sql,Object... params) {
        Row row=dqone(mandatory,sql,params);
        if (row==null) { return null; }
        return row.getInt();
    }
    public static Long dql(boolean mandatory,String sql,Object... params) {
        Row row=dqone(mandatory,sql,params);
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
    public static String dqs(boolean mandatory,String sql,Object... params) {
        Row row=dqone(mandatory,sql,params);
        if (row==null) { return null; }
        return row.getString();
    }

    public static Row getLock(String tablename,int keyvalue) {
        return dqone(true,"select lockedby,lockeduntil,lockedserial from "+tablename+" where id=?",keyvalue);
    }
    private static final Random random=new Random();
    public static int lock(String tablename,int keyvalue) { return lock(tablename,keyvalue,30); }
    /** Uses the write consistency property of MariaDB to synchronise a lock across the database.
     * The target table must use "id", a numeric primary key, as well as having lockedby (int), lockeduntil (int) and lockedserial (int).
     * The same serial should be used for the unlock.
     * Uses "Get old data" "Update lock where data+lock=olddata+oldlock" "Get data and confirm".  Since the updates must be ordered across the cluster
     * (the point of maria db) only one update will match its where clause, so locking is unique.
     * There is testing code for this in the top of the IPC initialisation code.
     * LOCKS LAST NO MORE THAN 30 SECONDS! then they're assumed expired.  Be quick.
     * Remember to unlock, don't just let it expire, thats antisocial.
     * @param tablename Table name to lock
     * @param keyvalue ID number for the row to lock
     * @return The serial for the lock
     */
    public static int lock(String tablename,int keyvalue,int lockdurationseconds) {
        // discover current lock state
        int serial=random.nextInt();
        Row row=getLock(tablename,keyvalue);
        int lockedby=row.getInt("lockedby");
        int lockeduntil=row.getInt("lockeduntil");
        int lockedserial=row.getInt("lockedserial");
        if (lockeduntil>UnixTime.getUnixTime() && lockedby>=0) { 
            //lock is claimed
            throw new LockException("Already locked by "+Config.getNodeName(lockedby)+" (we are "+Config.getNodeName()+")");
        }
        // lock is unclaimed or expired, try an update to claim it
        d("update "+tablename+" set lockedby=?,lockeduntil=?,lockedserial=? where id=? and lockedby=? and lockeduntil=? and lockedserial=?",Config.getNode(),UnixTime.getUnixTime()+lockdurationseconds,serial,keyvalue,lockedby,lockeduntil,lockedserial);
        // see if the update actually worked
        row=getLock(tablename,keyvalue);
        lockedby=row.getInt("lockedby");
        lockeduntil=row.getInt("lockeduntil");
        lockedserial=row.getInt("lockedserial");
        if (lockedby!=Config.getNode()) {
            throw new LockException("Failed to claim lock, attempted but claimed by "+Config.getNodeName(lockedby)+" (we are "+Config.getNodeName()+")");
        }
        if (lockeduntil<(UnixTime.getUnixTime()+10)) { 
            throw new LockException("Failed to claim lock, we hold it but it has little time left on it(?)");
        }
        if (lockedserial!=serial) { throw new LockException("Lock is held by this node, but in use by a different process"); }
        // we hold the row lock!
        return serial;
    }
    public static void extendLock(String tablename,int keyvalue,int serial,int lockdurationseconds) {
        Row row=getLock(tablename,keyvalue);
        int lockedby=row.getInt("lockedby");
        int lockeduntil=row.getInt("lockeduntil");
        int lockedserial=row.getInt("lockedserial");
        if (lockedby!=Config.getNode()) { throw new SystemException("Extending a lock we do not hold?"); }
        if (lockeduntil<UnixTime.getUnixTime()) { throw new SystemException("Extending a lock that expired?"); }
        if (lockedserial!=serial) { throw new SystemException("Extending a lock with the wrong serial?"); }
        DB.d("update "+tablename+" set lockeduntil=? where id=? and lockedby=? and lockeduntil=? and lockedserial=?",UnixTime.getUnixTime()+lockdurationseconds,keyvalue,lockedby,lockeduntil,lockedserial);
        row=getLock(tablename,keyvalue);
        if (row.getInt("lockedby")!=Config.getNode()) { throw new SystemException("Lock lost to other node while extending it"); }
        if (row.getInt("lockedserial")!=serial) { throw new SystemException("Lock serial lost during extension"); }
    }
    /** You must supply the serial you locked with.  Unlocking without the right serial is an Exception.
     * 
     * @param tablename Table name 
     * @param keyvalue Row id number to unlock
     * @param serial Current/valid serial number for the lock
     */
    public static void unlock(String tablename,int keyvalue,int serial) {
        Row row=getLock(tablename,keyvalue);
        int lockedby=row.getInt("lockedby");
        int lockeduntil=row.getInt("lockeduntil");
        int lockedserial=row.getInt("lockedserial");
        if (lockedby!=Config.getNode()) {
            throw new SystemException("Attempt to release lock held by node "+Config.getNodeName(lockedby)+" (we are "+Config.getNodeName()+")");
        }
        if (lockeduntil<UnixTime.getUnixTime()) {
            throw new SystemException("Attempt to release lock that expired "+(UnixTime.getUnixTime()-lockeduntil)+" seconds ago"); 
        }
        if (lockedserial!=serial) {
            throw new SystemException("Attempt to release lock with wrong serial ("+lockedserial+"!="+serial+")");
        }
        DB.d("update "+tablename+" set lockedby=-1,lockeduntil=0,lockedserial=0 where id=? and lockedby=? and lockeduntil=? and lockedserial=?",keyvalue,lockedby,lockeduntil,serial);
        // released lock :)
    }

    static void register(String sourcename, DBConnection aThis) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
