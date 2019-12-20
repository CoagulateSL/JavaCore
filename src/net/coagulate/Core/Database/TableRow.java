package net.coagulate.Core.Database;

import net.coagulate.Core.Exceptions.System.SystemBadValueException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Iain Price
 */
public abstract class TableRow extends Table {
	// use of the factory is of course optional :)
	protected static final Map<String,Map<Integer,TableRow>> factory=new TreeMap<>();
	final int id;

	public TableRow(final int id) { this.id=id; }

	/**
	 * Thread safe putter into the factory.  Also the getter.
	 * NOTE - this method returns an object.  this may not be the same as the object you are putting, if someone beat you to it.
	 * this is how we avoid race conditions creating two copies of the same, only one call can be in factoryPut() at once, whichever
	 * one gets there first will be stored, and the 2nd caller will simply get the first "put"s object back, which it should
	 * use in preference to its own created one.
	 *
	 * @param type  Class name for object type
	 * @param id    ID number
	 * @param store DBObject or subclass to store
	 *
	 * @return DBObject that should be used, not necessarily the one that was stored
	 */
	protected static synchronized TableRow factoryPut(final String type,
	                                                  final int id,
	                                                  final TableRow store) {
		if (id==0) {
			throw new SystemBadValueException("ID zero is expressly prohibited, does not exist, and suggests a programming bug.");
		}
		if (!factory.containsKey(type)) {
			// we need this to avoid null pointer below
			final Map<Integer,TableRow> innermap=new TreeMap<>();
			factory.put(type,innermap);
		}
		// does this ID exist already
		if (!factory.get(type).containsKey(id)) {
			// no - store it
			factory.get(type).put(id,store);
		}
		// return, either the previous value, or the one we just put there
		return factory.get(type).get(id);
	}

	@Nonnull
	public abstract String getIdColumn();

	public int getId() { return id; }

	@Nullable
	public String getStringNullable(final String column) {
		return dqs("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	@Nonnull
	public String getString(final String column) {
		final String ret=getStringNullable(column);
		if (ret==null) {
			throw new NoDataException("Null value for get string from "+getTableName()+" columne "+getIdColumn()+" id "+getId());
		}
		return ret;
	}

	@Nullable
	public Integer getIntNullable(final String column) {
		return dqi("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	public int getInt(final String column) {
		final Integer i=getIntNullable(column);
		if (i==null) {
			throw new NoDataException("Null value for get integer from "+getTableName()+" columne "+getIdColumn()+" id "+getId());
		}
		return i;
	}

	@Nullable
	public Float getFloatNullable(final String column) {
		return dqf("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	public float getFloat(final String column) {
		final Float ret=getFloatNullable(column);
		if (ret==null) {
			throw new NoDataException("Null value for get float from "+getTableName()+" columne "+getIdColumn()+" id "+getId());
		}
		return ret;
	}

	@Nullable
	public Long getLongNullable(final String column) {
		return dql("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	public long getLong(final String column) {
		final Long ret=getLongNullable(column);
		if (ret==null) {
			throw new NoDataException("Null value for get long from "+getTableName()+" columne "+getIdColumn()+" id "+getId());
		}
		return ret;
	}

	public boolean getBool(final String columnname) {
		final Integer val=getIntNullable(columnname);
		if (val==null || val==0) { return false; }
		if (val==1) { return true; }
		throw new DBException("Unexpected value "+val+" parsing DB boolean field selfmodify on attribute "+this);
	}

	public void set(final String columnname,
	                final Boolean value) { set(columnname,(value?1:0)); }

	// we use a factory style design to make sure that the same object is always returned
	// this is where we store our factory data, indexed by Type (string), ID (int) and then the DBObject subclass

	public void set(final String columnname,
	                final String value) { d("update "+getTableName()+" set "+columnname+"=? where "+getIdColumn()+"=?",value,getId()); }

	public void set(final String columnname,
	                final Integer value) { d("update "+getTableName()+" set "+columnname+"=? where "+getIdColumn()+"=?",value,getId()); }

	@Nullable
	public byte[] getBytesNullable(final String columnname) {
		return dqbyte("select "+columnname+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	@Nonnull
	public byte[] getBytes(final String columnname) {
		byte[] ret=dqbyte("select "+columnname+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
		if (ret==null) { ret=new byte[0]; }
		return ret;
	}
}
