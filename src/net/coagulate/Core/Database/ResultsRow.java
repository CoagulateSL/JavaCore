package net.coagulate.Core.Database;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents a row in a set of database results.
 */
public class ResultsRow {
	private final Map<String,String> row=new TreeMap<>();
	private final Map<String,byte[]> byteform=new TreeMap<>();

	/**
	 * Construct the row from a resultset.
	 *
	 * @param rs Resultset to read the row from
	 */
	public ResultsRow(@Nonnull final ResultSet rs) {
		try {
			final ResultSetMetaData rsmd=rs.getMetaData();
			for (int i=1;i<=rsmd.getColumnCount();i++) {
				byteform.put(rsmd.getColumnName(i),rs.getBytes(i));
				row.put(rsmd.getColumnName(i),rs.getString(i));
			}
		}
		catch (@Nonnull final SQLException ex) {
			throw new DBException("Exception unpacking result set",ex);
		}
	}

	// -------------------------- STRING FUNCTIONS --------------------------

	// ---------- INSTANCE ----------

	/**
	 * Returns the string contents of the given column for the row.
	 *
	 * @param column The name of the column to query
	 *
	 * @return The contents of the column, possibly null
	 *
	 * @throws NoDataException If the column name doesn't exist
	 */
	@Nullable
	public String getStringNullable(@Nonnull final String column) {
		if (!row.containsKey(column)) { throw new NoDataException("No such column "+column); }
		return row.get(column);
	}

	/**
	 * Returns the string contents of the only column in the row.
	 *
	 * @param column The name of the column to query
	 *
	 * @return The string content of the cell, never null.
	 *
	 * @throws NoDataException                If the column name doesn't exist
	 * @throws DBUnexpectedNullValueException If there is a null value present
	 */
	@Nonnull
	public String getString(@Nonnull final String column) {
		final String s=getStringNullable(column);
		if (s==null) { throw new DBUnexpectedNullValueException("Got null DB/string where not expected"); }
		return s;
	}

	/**
	 * Returns the string contents of the only column in the row.
	 *
	 * @return The string content of the cell, possibly null.
	 *
	 * @throws TooMuchDataException If there is more than one column in the results
	 */
	@Nullable
	public String getStringNullable() {
		if (row.size()!=1) { throw new TooMuchDataException("Column count !=1 - "+row.size()); }
		for (final String value: row.values()) { return value; }
		return null;
	}

	/**
	 * Returns the string contents of the only column in the row.
	 *
	 * @return The string content of the cell, never null.
	 *
	 * @throws TooMuchDataException           If there is more than one column in the results
	 * @throws DBUnexpectedNullValueException If there is a null value present
	 */
	@Nonnull
	public String getString() {
		final String s=getStringNullable();
		if (s==null) { throw new DBUnexpectedNullValueException("Got null DB/string where not expected"); }
		return s;
	}

	// -------------------------- INTEGER FUNCTIONS --------------------------

	/**
	 * Returns the integer contents of the given column for the row.
	 *
	 * @param column The name of the column to query
	 *
	 * @return The contents of the column, possibly null
	 *
	 * @throws NoDataException If the column name doesn't exist
	 */
	@Nullable
	public Integer getIntNullable(@Nonnull final String column) {
		final String result=getStringNullable(column);
		if (result==null) { return null; }
		return Integer.parseInt(result);
	}

	/**
	 * Returns the string contents of the only column in the row.
	 *
	 * @param column The name of the column to query
	 *
	 * @return The string content of the cell, never null.
	 *
	 * @throws NoDataException                If the column name doesn't exist
	 * @throws DBUnexpectedNullValueException If there is a null value present
	 */
	public int getInt(final String column) {
		final Integer result=getIntNullable(column);
		if (result==null) { throw new DBUnexpectedNullValueException("Got null value for integer column "+column); }
		return result;
	}

	/**
	 * Returns the integer contents of the only column in the row.
	 *
	 * @return The integer content of the cell, never null.
	 *
	 * @throws TooMuchDataException If there is more than one column in the results
	 */
	@Nullable
	public Integer getIntNullable() {
		if (getStringNullable()==null) { return null; }
		return Integer.parseInt(getStringNullable());
	}

	/**
	 * Returns the integer contents of the only column in the row.
	 *
	 * @return The integer content of the cell, never null.
	 *
	 * @throws TooMuchDataException           If there is more than one column in the results
	 * @throws DBUnexpectedNullValueException If there is a null value present
	 */
	public int getInt() {
		final Integer result=getIntNullable();
		if (result==null) { throw new DBUnexpectedNullValueException("Got null value for integer default column"); }
		return result;
	}

	// -------------------------- BOOLEAN FUNCTIONS --------------------------

	/**
	 * Returns the boolean contents of the given column for the row.
	 *
	 * @param column The name of the column to query
	 *
	 * @return The contents of the column, possibly null
	 *
	 * @throws NoDataException If the column name doesn't exist
	 */
	@Nullable
	public Boolean getBoolNullable(@Nonnull final String column) {
		if (getStringNullable(column)==null) { return null; }
		return "1".equals(getString(column));
	}

	/**
	 * Returns the boolean contents of the only column in the row.
	 *
	 * @return The boolean content of the cell, never null.
	 *
	 * @throws TooMuchDataException If there is more than one column in the results
	 */
	@Nullable
	public Boolean getBoolNullable() {
		if (getStringNullable()==null) { return null; }
		return "1".equals(getStringNullable());
	}

	/**
	 * Returns the boolean contents of the only column in the row.
	 *
	 * @param column The name of the column to query
	 *
	 * @return The boolean content of the cell, never null.
	 *
	 * @throws NoDataException                If the column name doesn't exist
	 * @throws DBUnexpectedNullValueException If there is a null value present
	 */
	public boolean getBool(@Nonnull final String column) {
		final Boolean b=getBoolNullable(column);
		if (b==null) { throw new DBUnexpectedNullValueException("Got null value for boolean column "+column); }
		return b;
	}

	/**
	 * Returns the boolean contents of the only column in the row.
	 *
	 * @return The boolean content of the cell, never null.
	 *
	 * @throws TooMuchDataException           If there is more than one column in the results
	 * @throws DBUnexpectedNullValueException If there is a null value present
	 */
	public boolean getBool() {
		final Boolean b=getBoolNullable();
		if (b==null) { throw new DBUnexpectedNullValueException("Got null value for boolean column"); }
		return b;
	}

	// -------------------------- FLOAT FUNCTIONS --------------------------

	/**
	 * Returns the float contents of the given column for the row.
	 *
	 * @param column The name of the column to query
	 *
	 * @return The contents of the column, possibly null
	 *
	 * @throws NoDataException If the column name doesn't exist
	 */
	@Nullable
	public Float getFloatNullable(@Nonnull final String column) {
		if (getStringNullable(column)==null) { return null; }
		return Float.parseFloat(getString(column));
	}

	/**
	 * Returns the float contents of the only column in the row.
	 *
	 * @return The float content of the cell, never null.
	 *
	 * @throws TooMuchDataException If there is more than one column in the results
	 */
	@Nullable
	public Float getFloatNullable() {
		if (getStringNullable()==null) { return null; }
		return Float.parseFloat(getStringNullable());
	}

	/**
	 * Returns the float contents of the only column in the row.
	 *
	 * @param column The name of the column to query
	 *
	 * @return The float content of the cell, never null.
	 *
	 * @throws NoDataException                If the column name doesn't exist
	 * @throws DBUnexpectedNullValueException If there is a null value present
	 */
	public float getFloat(@Nonnull final String column) {
		final Float f=getFloatNullable(column);
		if (f==null) { throw new DBUnexpectedNullValueException("Unexpected null value in column "+column); }
		return f;
	}

	/**
	 * Returns the float contents of the only column in the row.
	 *
	 * @return The float content of the cell, never null.
	 *
	 * @throws TooMuchDataException           If there is more than one column in the results
	 * @throws DBUnexpectedNullValueException If there is a null value present
	 */
	public float getFloat() {
		final Float f=getFloatNullable();
		if (f==null) { throw new DBUnexpectedNullValueException("Got null value for float column"); }
		return f;
	}

	// -------------------------- LONG FUNCTIONS --------------------------

	/**
	 * Returns the long contents of the given column for the row.
	 *
	 * @param column The name of the column to query
	 *
	 * @return The contents of the column, possibly null
	 *
	 * @throws NoDataException If the column name doesn't exist
	 */
	@Nullable
	public Long getLongNullable(@Nonnull final String column) {
		if (getStringNullable(column)==null) { return null; }
		return Long.parseLong(getString(column));
	}

	/**
	 * Returns the long contents of the only column in the row.
	 *
	 * @return The long content of the cell, never null.
	 *
	 * @throws TooMuchDataException If there is more than one column in the results
	 */
	@Nullable
	public Long getLongNullable() {
		if (getStringNullable()==null) { return null; }
		return Long.parseLong(getStringNullable());
	}

	/**
	 * Returns the long contents of the only column in the row.
	 *
	 * @param column The name of the column to query
	 *
	 * @return The long content of the cell, never null.
	 *
	 * @throws NoDataException                If the column name doesn't exist
	 * @throws DBUnexpectedNullValueException If there is a null value present
	 */
	public long getLong(@Nonnull final String column) {
		final Long f=getLongNullable(column);
		if (f==null) { throw new DBUnexpectedNullValueException("Unexpected null value in column "+column); }
		return f;
	}

	/**
	 * Returns the long contents of the only column in the row.
	 *
	 * @return The long content of the cell, never null.
	 *
	 * @throws TooMuchDataException           If there is more than one column in the results
	 * @throws DBUnexpectedNullValueException If there is a null value present
	 */
	public long getLong() {
		final Long f=getLongNullable();
		if (f==null) { throw new DBUnexpectedNullValueException("Got null value for long column"); }
		return f;
	}

	// -------------------------- BYTE FUNCTIONS --------------------------

	@Nonnull
	public byte[] getBytes() {
		if (byteform.size()!=1) { throw new DBException("Column count !=1 - "+byteform.size()); }
		for (final byte[] bytes: byteform.values()) {
			if (bytes==null) { return new byte[0]; }
			return bytes;
		}
		return new byte[0];
	}

	@Nonnull
	public byte[] getBytes(@Nonnull final String s) { return byteform.get(s); }


	/**
	 * Gets a list of all the columns in this row
	 *
	 * @return Set of Strings of column names
	 */
	@Nonnull
	public Set<String> keySet() { return row.keySet(); }

	@Nonnull
	@Override
	public String toString() {
		String output="[";
		for (final String k: keySet()) {
			if (!"[".equals(output)) { output=output+", "; }
			output=output+k+"="+getStringNullable(k);
		}
		output+="]";
		return output;
	}

}
