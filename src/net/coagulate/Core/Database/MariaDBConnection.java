package net.coagulate.Core.Database;

import org.mariadb.jdbc.MariaDbPoolDataSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import static java.util.logging.Level.CONFIG;
import static java.util.logging.Level.SEVERE;

/**
 * @author Iain Price
 */
public class MariaDBConnection extends DBConnection {
	private static final boolean TABLECOUNT = true;
	@Nullable
	private MariaDbPoolDataSource pool;

	public MariaDBConnection(String sourcename, String host, String username, String password, String dbname) {
		this(sourcename, "jdbc:mariadb://" + host + "/" + dbname + "?user=" + username + "&password=" + password + "&maxPoolSize=10&connectTimeout=5000");
	}


	public MariaDBConnection(String name, String jdbc) {
		super(name);
		try {
			pool = new MariaDbPoolDataSource(jdbc);
			if (!test()) { throw new SQLException("Failed to count(*) on table ping which should have one row only"); }
			// pointless stuff that slows us down =)
			if (TABLECOUNT) {
				Results tables = dq("show tables");
				Map<String, Integer> notempty = new TreeMap<>();
				for (ResultsRow r : tables) {
					String tablename = r.getString();
					int rows = dqi(true, "select count(*) from " + tablename);
					if (rows > 0) {
						notempty.put(tablename, rows);
					} else {
						logger.fine("Table " + tablename + " is empty");
					}
				}
				for (Map.Entry<String, Integer> entry : notempty.entrySet()) {
					int rows = entry.getValue();
					logger.fine("Table " + entry.getKey() + " contains " + rows + " entries");
				}
			}

		} catch (@Nonnull SQLException | DBException ex) {
			logger.log(SEVERE, "Failed connectivity test to database", ex);
			System.exit(1);
		}
		register();
		logger.config("Database connection [" + name + "] established and responding to test statements.");

	}

	public void shutdown() {
		logger.config("Closing database connection");
		try {
			if (pool != null) {
				pool.close();
				pool = null;
			}
		} catch (NullPointerException e) {} // hmm
		catch (Exception e) { logger.log(CONFIG, "Error closing DB connection: " + e.getLocalizedMessage()); }
	}

	public Connection getConnection() {
		try { return pool.getConnection(); } catch (SQLException e) {
			throw new DBException("Unable to get database pooled connection", e);
		}
	}

}
