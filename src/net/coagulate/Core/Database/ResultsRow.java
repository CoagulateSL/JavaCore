package net.coagulate.Core.Database;

/**
 * Represents a row in a set of database results.
 *
 * @author Iain Price <gphud@predestined.net>
 */

import net.coagulate.Core.Tools.SystemException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ResultsRow {
	private final Map<String, String> row = new TreeMap<>();
	private final Map<String,byte[]> byteform=new TreeMap<>();
	/** Construct the frow from a resultset.
	 *
	 * @param rs Resultset to read the row from
	 */
	public ResultsRow(@Nonnull ResultSet rs) throws DBException {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				byteform.put(rsmd.getColumnName(i),rs.getBytes(i));
				row.put(rsmd.getColumnName(i), rs.getString(i));
			}
		} catch (SQLException ex) {
			throw new DBException("Exception unpacking result set", ex);
		}
	}

	public String getStringNullable(String s) { return row.get(s); }

	@Nullable
	public Integer getIntNullable(String s) {
		String result = getStringNullable(s);
		if (result == null) { return null; }
		return Integer.parseInt(result);
	}

	public int getInt() {
		Integer result= getIntNullable();
		if (result==null) { throw new NoDataException("Got null value for integer default column"); }
		return result;
	}

	public int getInt(String s) {
		Integer result = getIntNullable(s);
		if (result == null) { throw new NoDataException("Got null value for integer column "+s); }
		return result;
	}
	@Nullable
	public Boolean getBool(String s) {
		if (getStringNullable(s)==null) { return null; }
		if (getStringNullable(s).equals("1")) { return true; } return false;
	}

	public boolean getBoolNoNull(String s) {
		Boolean b=getBool(s);
		if (b==null) { throw new NoDataException("Got null value for boolean column "+s); }
		return b;
	}
	public float getFloat(String s) { return Float.parseFloat(getStringNullable(s)); }

	@Nullable
	public String getStringNullable() {
		if (row.size() != 1) { throw new DBException("Column count !=1 - " + row.size()); }
		for (String value : row.values()) { return value; }
		return null;
	}

	@Nonnull
	public String getString() {
		String s=getStringNullable();
		if (s==null) { throw new SystemException("Got null DB/string where not expected"); }
		return s;
	}

	@Nonnull
	public String getString(String column) {
		String s=getStringNullable(column);
		if (s==null) { throw new SystemException("Got null DB/string where not expected"); }
		return s;
	}

	@Nullable
	public Boolean getBool() {
		if (getStringNullable()==null) { return null; }
		if (getStringNullable().equals("1")) { return true; } return false;
	}

	@Nullable
	public Integer getIntNullable() {
		if (getStringNullable() == null) { return null; }
		return Integer.parseInt(getStringNullable());
	}

	@Nullable
	public Long getLong() {
		if (getStringNullable() == null) { return null; }
		return Long.parseLong(getStringNullable());
	}

	@Nullable
	public Float getFloat() {
		if (getStringNullable() == null) { return null; }
		return Float.parseFloat(getStringNullable());
	}

	@Nonnull
	public Set<String> keySet() { return row.keySet(); }

	@Nonnull
	@Override
	public String toString() {
		String output = "[";
		for (String k : keySet()) {
			if (!"[".equals(output)) { output = output + ", "; }
			output = output + k + "=" + getStringNullable(k);
		}
		output += "]";
		return output;
	}

	@Nullable
	public byte[] getBytes() {
		if (byteform.size() != 1) { throw new DBException("Column count !=1 - " + byteform.size()); }
		for (byte[] bytes : byteform.values()) { return bytes; }
		return null;
	}

	public byte[] getBytes(String s) { return byteform.get(s); }
}
