package net.coagulate.Core.Database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import static java.util.logging.Level.SEVERE;
import org.mariadb.jdbc.MariaDbPoolDataSource;

/**
 *
 * @author Iain Price
 */
public class MariaDBConnection extends DBConnection {

    private MariaDbPoolDataSource pool;
    

    public void shutdown() {
        logger.config("Closing database connection");
        if (pool!=null) { pool.close(); pool=null; }
    }
    
    public MariaDBConnection(String name,String jdbc) {
        super(name);
        try {
            pool = new MariaDbPoolDataSource(jdbc);
            if (!test()) { throw new SQLException("Failed to count(*) on table ping which should have one row only"); }
            // pointless stuff that slows us down =)
            Results tables=dq("show tables");
            Map<String,Integer> notempty=new TreeMap<>();
            for (ResultsRow r:tables) {
                String tablename=r.getString();
                int rows=dqi(true,"select count(*) from "+tablename);
                if (rows>0) {
                    notempty.put(tablename,rows);
                } else { 
                    logger.config("Table "+tablename+" is empty");
                }
            }
            for (String tablename:notempty.keySet()) {
                int rows=notempty.get(tablename);
                logger.config("Table "+tablename+" contains "+rows+" entries");
            }

        } catch (SQLException|DBException ex) {
            logger.log(SEVERE,"Failed connectivity test to database",ex);
            System.exit(1);
        }
        register();
        logger.config("Database connection ["+name+"] established and responding to test statements.");
        
    }

    public Connection getConnection() {
        try { return pool.getConnection(); }
        catch (SQLException e) { throw new DBException("Unable to get database pooled connection",e); }
    }
  
}