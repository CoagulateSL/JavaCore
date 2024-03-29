package net.coagulate.Core.Database;

import net.coagulate.Core.Exceptions.System.SystemInitialisationException;
import org.apache.commons.dbcp2.BasicDataSource;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class MySqlDBConnection extends DBConnection {
	
	private final BasicDataSource dataSource=new BasicDataSource();
	
	public MySqlDBConnection(final String sourceName,final String jdbc) {
		super(sourceName);
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl(jdbc);
		dataSource.setDefaultAutoCommit(true);
		dataSource.setAutoCommitOnReturn(true);
		register();
	}
	
	@Override
	public void shutdown() {
		try {
			dataSource.close();
		} catch (final SQLException e) {
			logger.log(Level.WARNING,"Failed to close datasource "+getName(),e);
		}
	}
	
	@Nonnull
	@Override
	public Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (final SQLException e) {
			throw new SystemInitialisationException("Unable to get database pooled connection",e);
		}
	}
}
