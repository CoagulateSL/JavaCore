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

	protected TableRow() {
		super();
		id=-1;
	}

	// ----- Internal Statics -----

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
	@Nonnull
	protected static synchronized TableRow factoryPut(@Nonnull final String type,
	                                                  final int id,
	                                                  @Nonnull final TableRow store) {
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

	// ---------- INSTANCE ----------

	/**
	 * Defines the name of the ID (primary key) column for this table.
	 *
	 * @return The name of the ID column
	 */
	@Nonnull
	public abstract String getIdColumn();

	public int getId() { return id; }

	/**
	 * Convenience method for getting a string.
	 *
	 * @param column Name of the column to read
	 *
	 * @return The string form of the column.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public String getStringNullable(@Nonnull final String column) {
		return dqs("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	/**
	 * Convenience method for getting a string.
	 *
	 * @param column Name of the column to read
	 *
	 * @return The string form of the column.
	 *
	 * @throws NoDataException      if there are no results or the contents of the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nonnull
	public String getString(@Nonnull final String column) {
		return dqsnn("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	/**
	 * Convenience method for getting an integer.
	 *
	 * @param column Name of the column to read
	 *
	 * @return The integer form of the column.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public Integer getIntNullable(@Nonnull final String column) {
		return dqi("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	/**
	 * Convenience method for getting a string.
	 *
	 * @param column Name of the column to read
	 *
	 * @return The string form of the column.
	 *
	 * @throws NoDataException      if there are no results or the contents of the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	public int getInt(@Nonnull final String column) {
		return dqinn("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	/**
	 * Convenience method for getting a float.
	 *
	 * @param column Name of the column to read
	 *
	 * @return The float form of the column.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public Float getFloatNullable(@Nonnull final String column) {
		return dqf("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	/**
	 * Convenience method for getting a float.
	 *
	 * @param column Name of the column to read
	 *
	 * @return The float form of the column.
	 *
	 * @throws NoDataException      if there are no results or the contents of the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	public float getFloat(@Nonnull final String column) {
		return dqfnn("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	/**
	 * Convenience method for getting a long.
	 *
	 * @param column Name of the column to read
	 *
	 * @return The long form of the column.  Can be null only if the cell's contents are null.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nullable
	public Long getLongNullable(@Nonnull final String column) {
		return dql("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	/**
	 * Convenience method for getting a long.
	 *
	 * @param column Name of the column to read
	 *
	 * @return The long form of the column.
	 *
	 * @throws NoDataException      if there are no results or the contents of the cell is null
	 * @throws TooMuchDataException if there are multiple results
	 */
	public long getLong(@Nonnull final String column) {
		return dqlnn("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}


	/**
	 * Convenience method for getting a boolean.
	 *
	 * @param column Name of the column to read
	 *
	 * @return The boolean form of the column.  0/null maps to false and 1 maps to true.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	public boolean getBool(@Nonnull final String column) {
		final Integer val=getIntNullable(column);
		if (val==null || val==0) { return false; }
		if (val==1) { return true; }
		throw new DBException("Unexpected value "+val+" parsing DB boolean field selfmodify on attribute "+this);
	}

	/**
	 * Convenience method for getting a byte array.
	 *
	 * @param column Name of the column to read
	 *
	 * @return The byte array form of the column.
	 *
	 * @throws NoDataException      if there are no results
	 * @throws TooMuchDataException if there are multiple results
	 */
	@Nonnull
	public byte[] getBytes(@Nonnull final String column) {
		return dqbytenn("select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId());
	}

	/**
	 * Set a column with a boolean value.
	 *
	 * @param columnname Name of the column to set
	 * @param value      The boolean value to set
	 */
	public void set(@Nonnull final String columnname,
	                @Nullable final Boolean value) { set(columnname,(value!=null && value?1:0)); }

	/**
	 * Set a column with a string value.
	 *
	 * @param columnname The name of the column to set
	 * @param value      The string value to store
	 */
	public void set(@Nonnull final String columnname,
	                @Nullable final String value) {
		d("update "+getTableName()+" set "+columnname+"=? where "+getIdColumn()+"=?",value,getId());
	}

	/**
	 * Set a column with an integer value.
	 *
	 * @param columnname The name of the column to set
	 * @param value      The integer value to store
	 */
	public void set(@Nonnull final String columnname,
	                @Nullable final Integer value) {
		d("update "+getTableName()+" set "+columnname+"=? where "+getIdColumn()+"=?",value,getId());
	}

}
