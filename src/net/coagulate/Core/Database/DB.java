package net.coagulate.Core.Database;

import net.coagulate.Core.Tools.SystemException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Handles the database connection
 * Uses a database pool to create connections.
 * Takes incoming parameterised SQL queries for preparedStatement
 * Unmarshals results into hashmap, array of arrays, single value, whatever, lots of tedious DB support methods, "getInt(sql)" etc
 * Releases result-sets etc once unmarshalled.
 * <p>
 * NOTE: The database is asynchronously replicating, data should not be cached and used for updates for any extensive period of time.
 * be aware other threads may have modified the data in the mean time, ensure you accomodate or override the values as appropriate.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public abstract class DB {
	static final long SLOWQUERYTHRESHOLD_QUERY = 100;
	static final long SLOWQUERYTHRESHOLD_UPDATE = 250;
	static final boolean sqldebug_queries = false;
	static final boolean sqldebug_commands = false;


	private static final Map<String, DBConnection> datasources = new HashMap<>();

	static void register(String sourcename, DBConnection connection) {
		if (datasources.containsKey(sourcename)) {
			Logger.getLogger(DB.class.getName()).warning("Re-registering DB connection " + sourcename);
			DBConnection outgoing = datasources.get(sourcename);
			outgoing.shutdown();
		}
		datasources.put(sourcename, connection);
	}

	public static DBConnection get(String datasourcename) {
		if (!datasources.containsKey(datasourcename)) {
			throw new SystemException("Attempt to retrieve non-existant data source " + datasourcename);
		}
		return datasources.get(datasourcename);
	}

	public static Set<DBConnection> get() {
		Set<DBConnection> connections = new HashSet<>();
		connections.addAll(datasources.values());
		return connections;
	}

	public static void shutdown() {
		Set<String> names = new HashSet<>();
		names.addAll(datasources.keySet());
		for (String source : names) {
			// Logger.getLogger(DB.class.getName()).config("Closing database connection "+source); // logged by shutdown method
			datasources.get(source).shutdown();
			datasources.remove(names);
		}
	}

	public static boolean test() {
		for (DBConnection connection : datasources.values()) {
			if (!connection.test()) { return false; }
		}
		return true;
	}
}
