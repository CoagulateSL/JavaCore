package net.coagulate.Core.Database;

/**
 * Represents a row in a set of database results.
 *
 * @author Iain Price <gphud@predestined.net>
 */

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ResultsRow {
	private final Map<String, String> row = new TreeMap<>();

	/** Construct the frow from a resultset.
	 *
	 * @param rs Resultset to read the row from
	 */
	public ResultsRow(ResultSet rs) throws DBException {
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				row.put(rsmd.getColumnName(i), rs.getString(i));
			}
		} catch (SQLException ex) {
			throw new DBException("Exception unpacking result set", ex);
		}
	}

	public String getString(String s) { return row.get(s); }

	public Integer getInt(String s) {
		String result = getString(s);
		if (result == null) { return null; }
		return Integer.parseInt(result);
	}
	public Boolean getBool(String s) {
		if (getString(s)==null) { return null; }
		if (getString(s).equals("1")) { return true; } return false;
	}

	public float getFloat(String s) { return Float.parseFloat(getString(s)); }

	public String getString() {
		if (row.size() != 1) { throw new DBException("Column count !=1 - " + row.size()); }
		for (String s : row.keySet()) { return row.get(s); }
		return null;
	}

	public Boolean getBool() {
		if (getString()==null) { return null; }
		if (getString().equals("1")) { return true; } return false;
	}

	public Integer getInt() {
		if (getString() == null) { return null; }
		return Integer.parseInt(getString());
	}

	public Long getLong() {
		if (getString() == null) { return null; }
		return Long.parseLong(getString());
	}

	public Float getFloat() {
		if (getString() == null) { return null; }
		return Float.parseFloat(getString());
	}

	public Set<String> keySet() { return row.keySet(); }

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
}
