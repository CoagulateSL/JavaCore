package net.coagulate.Database;

import java.util.Random;
import net.coagulate.Core.SystemException;
import net.coagulate.Core.UnixTime;

/**
 *
 * @author Iain Price
 */
public abstract class StandardLockableTableRow extends StandardTableRow {
    public abstract int getNode();
    
    public ResultsRow getLock(String tablename,int keyvalue) {
        return getDatabase().dqone(true,"select lockedby,lockeduntil,lockedserial from "+tablename+" where id=?",keyvalue);
    }   

    private static final Random random=new Random();

    public int lock(String tablename,int keyvalue) { return lock(tablename,keyvalue,30); }
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
    public int lock(String tablename,int keyvalue,int lockdurationseconds) {
        // discover current lock state
        int serial=random.nextInt();
        ResultsRow row=getLock(tablename,keyvalue);
        int lockedby=row.getInt("lockedby");
        int lockeduntil=row.getInt("lockeduntil");
        int lockedserial=row.getInt("lockedserial");
        if (lockeduntil>UnixTime.getUnixTime() && lockedby>=0) { 
            //lock is claimed
            throw new LockException("Already locked by node "+lockedby+" (we are "+getNode()+")");
        }
        // lock is unclaimed or expired, try an update to claim it
        getDatabase().d("update "+tablename+" set lockedby=?,lockeduntil=?,lockedserial=? where id=? and lockedby=? and lockeduntil=? and lockedserial=?",getNode(),UnixTime.getUnixTime()+lockdurationseconds,serial,keyvalue,lockedby,lockeduntil,lockedserial);
        // see if the update actually worked
        row=getLock(tablename,keyvalue);
        lockedby=row.getInt("lockedby");
        lockeduntil=row.getInt("lockeduntil");
        lockedserial=row.getInt("lockedserial");
        if (lockedby!=getNode()) {
            throw new LockException("Failed to claim lock, attempted but claimed by node "+lockedby+" (we are "+getNode()+")");
        }
        if (lockeduntil<(UnixTime.getUnixTime()+10)) { 
            throw new LockException("Failed to claim lock, we hold it but it has little time left on it(?)");
        }
        if (lockedserial!=serial) { throw new LockException("Lock is held by this node, but in use by a different process"); }
        // we hold the row lock!
        return serial;
    }
    public void extendLock(String tablename,int keyvalue,int serial,int lockdurationseconds) {
        ResultsRow row=getLock(tablename,keyvalue);
        int lockedby=row.getInt("lockedby");
        int lockeduntil=row.getInt("lockeduntil");
        int lockedserial=row.getInt("lockedserial");
        if (lockedby!=getNode()) { throw new SystemException("Extending a lock we do not hold?"); }
        if (lockeduntil<UnixTime.getUnixTime()) { throw new SystemException("Extending a lock that expired?"); }
        if (lockedserial!=serial) { throw new SystemException("Extending a lock with the wrong serial?"); }
        getDatabase().d("update "+tablename+" set lockeduntil=? where id=? and lockedby=? and lockeduntil=? and lockedserial=?",UnixTime.getUnixTime()+lockdurationseconds,keyvalue,lockedby,lockeduntil,lockedserial);
        row=getLock(tablename,keyvalue);
        if (row.getInt("lockedby")!=getNode()) { throw new SystemException("Lock lost to other node while extending it"); }
        if (row.getInt("lockedserial")!=serial) { throw new SystemException("Lock serial lost during extension"); }
    }
    /** You must supply the serial you locked with.  Unlocking without the right serial is an Exception.
     * 
     * @param tablename Table name 
     * @param keyvalue ResultsRow id number to unlock
     * @param serial Current/valid serial number for the lock
     */
    public void unlock(String tablename,int keyvalue,int serial) {
        ResultsRow row=getLock(tablename,keyvalue);
        int lockedby=row.getInt("lockedby");
        int lockeduntil=row.getInt("lockeduntil");
        int lockedserial=row.getInt("lockedserial");
        if (lockedby!=getNode()) {
            throw new SystemException("Attempt to release lock held by node "+lockedby+" (we are "+getNode()+")");
        }
        if (lockeduntil<UnixTime.getUnixTime()) {
            throw new SystemException("Attempt to release lock that expired "+(UnixTime.getUnixTime()-lockeduntil)+" seconds ago"); 
        }
        if (lockedserial!=serial) {
            throw new SystemException("Attempt to release lock with wrong serial ("+lockedserial+"!="+serial+")");
        }
        getDatabase().d("update "+tablename+" set lockedby=-1,lockeduntil=0,lockedserial=0 where id=? and lockedby=? and lockeduntil=? and lockedserial=?",keyvalue,lockedby,lockeduntil,serial);
        // released lock :)
    }    
}
