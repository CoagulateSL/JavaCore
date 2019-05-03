package net.coagulate.Core.Database;

import net.coagulate.Core.Tools.SystemException;
import net.coagulate.Core.Tools.UnixTime;

import java.util.Random;

/**
 * @author Iain Price
 */
public abstract class StandardLockableTableRow extends StandardTableRow {

	private static final Random random = new Random();

	public StandardLockableTableRow(int id) {super(id);}

	public abstract int getNode();

	public ResultsRow getLock() {
		return getDatabase().dqone(true, "select lockedby,lockeduntil,lockedserial from " + getTableName() + " where " + getIdColumn() + "=?", getId());
	}

	public int lock() { return lock(30); }

	/**
	 * Uses the write consistency property of MariaDB to synchronise a lock across the GPHUD.getDB().
	 * The target table must use "id", a numeric primary key, as well as having lockedby (int), lockeduntil (int) and lockedserial (int).
	 * The same serial should be used for the unlock.
	 * Uses "Get old data" "Update lock where data+lock=olddata+oldlock" "Get data and confirm".  Since the updates must be ordered across the cluster
	 * (the point of maria db) only one update will match its where clause, so locking is unique.
	 * There is testing code for this in the top of the IPC initialisation code.
	 * LOCKS LAST NO MORE THAN 30 SECONDS! then they're assumed expired.  Be quick.
	 * Remember to unlock, don't just let it expire, thats antisocial.
	 *
	 * @return The serial for the lock
	 */
	public int lock(int lockdurationseconds) {
		// discover current lock state
		int serial = random.nextInt();
		ResultsRow row = getLock();
		int lockedby = row.getInt("lockedby");
		int lockeduntil = row.getInt("lockeduntil");
		int lockedserial = row.getInt("lockedserial");
		if (lockeduntil > UnixTime.getUnixTime() && lockedby >= 0) {
			//lock is claimed
			throw new LockException("Already locked by node " + lockedby + " (we are " + getNode() + ")");
		}
		// lock is unclaimed or expired, try an update to claim it
		getDatabase().d("update " + getTableName() + " set lockedby=?,lockeduntil=?,lockedserial=? where " + getIdColumn() + "=? and lockedby=? and lockeduntil=? and lockedserial=?", getNode(), UnixTime.getUnixTime() + lockdurationseconds, serial, getId(), lockedby, lockeduntil, lockedserial);
		// see if the update actually worked
		row = getLock();
		lockedby = row.getInt("lockedby");
		lockeduntil = row.getInt("lockeduntil");
		lockedserial = row.getInt("lockedserial");
		if (lockedby != getNode()) {
			throw new LockException("Failed to claim lock, attempted but claimed by node " + lockedby + " (we are " + getNode() + ")");
		}
		if (lockeduntil < (UnixTime.getUnixTime() + 10)) {
			throw new LockException("Failed to claim lock, we hold it but it has little time left on it(?)");
		}
		if (lockedserial != serial) {
			throw new LockException("Lock is held by this node, but in use by a different process");
		}
		// we hold the row lock!
		return serial;
	}

	public void extendLock(int serial, int lockdurationseconds) {
		ResultsRow row = getLock();
		int lockedby = row.getInt("lockedby");
		int lockeduntil = row.getInt("lockeduntil");
		int lockedserial = row.getInt("lockedserial");
		if (lockedby != getNode()) { throw new SystemException("Extending a lock we do not hold?"); }
		if (lockeduntil < UnixTime.getUnixTime()) { throw new SystemException("Extending a lock that expired?"); }
		if (lockedserial != serial) { throw new SystemException("Extending a lock with the wrong serial?"); }
		getDatabase().d("update " + getTableName() + " set lockeduntil=? where " + getIdColumn() + "=? and lockedby=? and lockeduntil=? and lockedserial=?", UnixTime.getUnixTime() + lockdurationseconds, getId(), lockedby, lockeduntil, lockedserial);
		row = getLock();
		if (row.getInt("lockedby") != getNode()) {
			throw new SystemException("Lock lost to other node while extending it");
		}
		if (row.getInt("lockedserial") != serial) { throw new SystemException("Lock serial lost during extension"); }
	}

	/**
	 * You must supply the serial you locked with.  Unlocking without the right serial is an Exception.
	 *
	 * @param serial    Current/valid serial number for the lock
	 */
	public void unlock(int serial) {
		ResultsRow row = getLock();
		int lockedby = row.getInt("lockedby");
		int lockeduntil = row.getInt("lockeduntil");
		int lockedserial = row.getInt("lockedserial");
		if (lockedby != getNode()) {
			throw new SystemException("Attempt to release lock held by node " + lockedby + " (we are " + getNode() + ")");
		}
		if (lockeduntil < UnixTime.getUnixTime()) {
			throw new SystemException("Attempt to release lock that expired " + (UnixTime.getUnixTime() - lockeduntil) + " seconds ago");
		}
		if (lockedserial != serial) {
			throw new SystemException("Attempt to release lock with wrong serial (" + lockedserial + "!=" + serial + ")");
		}
		getDatabase().d("update " + getTableName() + " set lockedby=-1,lockeduntil=0,lockedserial=0 where " + getIdColumn() + "=? and lockedby=? and lockeduntil=? and lockedserial=?", getId(), lockedby, lockeduntil, serial);
		// released lock :)
	}
}
