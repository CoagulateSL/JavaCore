package net.coagulate.Core.Database;

import net.coagulate.Core.Exceptions.System.SystemInitialisationException;
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
	private static final boolean TABLECOUNT=false; // just kinda spammy at this point, was interesting when the world was new(er)
	@Nullable
	private MariaDbPoolDataSource pool;

	public MariaDBConnection(final String sourcename,
	                         final String host,
	                         final String username,
	                         final String password,
	                         final String dbname) {
		this(sourcename,"jdbc:mariadb://"+host+"/"+dbname+"?user="+username+"&password="+password+"&minPoolSize=2&maxPoolSize=25&connectTimeout=5000");
	}


	public MariaDBConnection(final String name,
	                         final String jdbc) {
		super(name);
		try {
			pool=new MariaDbPoolDataSource(jdbc);
			if (!test()) { throw new SQLException("Failed to select 1 from database"); }
			// pointless stuff that slows us down =)
			if (TABLECOUNT) {
				final Results tables=dq("show tables");
				final Map<String,Integer> notempty=new TreeMap<>();
				for (final ResultsRow r: tables) {
					final String tablename=r.getStringNullable();
					final int rows=dqinn("select count(*) from "+tablename);
					if (rows>0) {
						notempty.put(tablename,rows);
					}
					else {
						logger.fine("Table "+tablename+" is empty");
					}
				}
				for (final Map.Entry<String,Integer> entry: notempty.entrySet()) {
					final int rows=entry.getValue();
					logger.fine("Table "+entry.getKey()+" contains "+rows+" entries");
				}
			}

		}
		catch (@Nonnull final SQLException|DBException ex) {
			logger.log(SEVERE,"Failed connectivity test to database",ex);
			System.exit(1);
		}
		register();
		logger.config("Database connection ["+name+"] established and responding to test statements.");

	}

	// ---------- INSTANCE ----------
	public void shutdown() {
		logger.config("Closing database connection");
		try {
			if (pool!=null) {
				pool.close();
				pool=null;
			}
		}
		catch (@Nonnull final NullPointerException e) {} // hmm
		catch (@Nonnull final Exception e) {
			logger.log(CONFIG,"Error closing DB connection: "+e.getLocalizedMessage());
		}
	}

	@Nonnull
	public Connection getConnection() {
		try {
			if (pool==null) { throw new SystemInitialisationException("DB Pool is not initialised (is null)"); }
			return pool.getConnection();
		}
		catch (@Nonnull final SQLException e) {
			throw new DBException("Unable to get database pooled connection",e);
		}
	}

}
