package net.coagulate.Core.Database;

/**
 * Represents a row in a set of database results.
 *
 * @author Iain Price <gphud@predestined.net>
 */

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

	public String getString(String s) { return row.get(s); }

	@Nullable
	public Integer getInt(String s) {
		String result = getString(s);
		if (result == null) { return null; }
		return Integer.parseInt(result);
	}
	@Nullable
	public Boolean getBool(String s) {
		if (getString(s)==null) { return null; }
		if (getString(s).equals("1")) { return true; } return false;
	}

	public float getFloat(String s) { return Float.parseFloat(getString(s)); }

	@Nullable
	public String getString() {
		if (row.size() != 1) { throw new DBException("Column count !=1 - " + row.size()); }
		for (String value : row.values()) { return value; }
		return null;
	}

	@Nullable
	public Boolean getBool() {
		if (getString()==null) { return null; }
		if (getString().equals("1")) { return true; } return false;
	}

	@Nullable
	public Integer getInt() {
		if (getString() == null) { return null; }
		return Integer.parseInt(getString());
	}

	@Nullable
	public Long getLong() {
		if (getString() == null) { return null; }
		return Long.parseLong(getString());
	}

	@Nullable
	public Float getFloat() {
		if (getString() == null) { return null; }
		return Float.parseFloat(getString());
	}

	@Nonnull
	public Set<String> keySet() { return row.keySet(); }

	@Nonnull
	@Override
	public String toString() {
		String output = "[";
		for (String k : keySet()) {
			if (!"[".equals(output)) { output = output + ", "; }
			output = output + k + "=" + getString(k);
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
