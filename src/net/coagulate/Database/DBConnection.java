package net.coagulate.Database;

import java.util.HashMap;
import java.util.Map;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;
import static net.coagulate.Database.DB.dqi;
import net.coagulate.SL.DBException;
import net.coagulate.SL.UserException;

/**
 *
 * @author Iain Price
 */
public abstract class DBConnection {
    
    private static final boolean logsql=true; public static boolean sqlLogging() { return logsql; }
    
    private Map<String,Integer> sqllog=new HashMap<>();
    private Map<String,Long> sqllogsum=new HashMap<>();
    
    public void getSqlLogs(Map<String,Integer> count,Map<String,Long> runtime) throws UserException {
        if (!logsql) { throw new UserException("SQL Auditing is not enabled"); }
        count.putAll(sqllog); runtime.putAll(sqllogsum);
    }

    public abstract void shutdown();

    private final String sourcename;    
    protected DBConnection(String sourcename) {
        this.sourcename=sourcename;
        logger=Logger.getLogger(this.getClass().getName()+"."+sourcename);
    }
    /** dont forget to call this during setup! */
    protected void register() { DB.register(sourcename,this); }

    private Logger logger;
    
    public boolean test() {
        try {
            int result=dqi(true,"select count(*) from ping");
            if (result!=1) { throw new DBException("Select count(*) from ping returned not 1 ("+result+")"); }
            return true;
        }
        catch (Exception e){ logger.log(SEVERE,"Database connectivity test failure",e); }
        return false;
    }

    
    
    
    
}
