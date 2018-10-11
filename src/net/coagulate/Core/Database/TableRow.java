package net.coagulate.Core.Database;

import java.util.Map;
import java.util.TreeMap;
import net.coagulate.GPHUD.SystemException;

/**
 *
 * @author Iain Price
 */
public abstract class TableRow extends Table {
    public abstract String getIdColumn();
    public abstract int getId();
    
    public String getString(String column) { return dqs(true,"select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId()); }
    public Integer getInt(String column) { return dqi(true,"select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId()); }
    public Long getLong(String column) { return dql(true,"select "+column+" from "+getTableName()+" where "+getIdColumn()+"=?",getId()); }
    
    // we use a factory style design to make sure that the same object is always returned
    // this is where we store our factory data, indexed by Type (string), ID (int) and then the DBObject subclass
    
    // use of the factory is of course optional :)
    protected static Map<String,Map<Integer,TableRow>> factory=new TreeMap<String,Map<Integer,TableRow>>();
    
    /** Thread safe putter into the factory.  Also the getter.
     * NOTE - this method returns an object.  this may not be the same as the object you are putting, if someone beat you to it.
     * this is how we avoid race conditions creating two copies of the same, only one call can be in factoryPut() at once, whichever
     * one gets there first will be stored, and the 2nd caller will simply get the first "put"s object back, which it should 
     * use in preference to its own created one.
     * @param type Class name for object type
     * @param id ID number
     * @param store DBObject or subclass to store
     * @return DBObject that should be used, not necessarily the one that was stored
     */
    protected static synchronized TableRow factoryPut(String type,int id,TableRow store) {
        if (id==0) { throw new SystemException("ID zero is expressly prohibited, does not exist, and suggests a programming bug."); }
        if (!factory.containsKey(type)) {
            // we need this to avoid null pointer below
            Map<Integer,TableRow> innermap=new TreeMap<>();
            factory.put(type,innermap);
        }
        // does this ID exist already
        if (!factory.get(type).containsKey(id)) {
            // no - store it
            factory.get(type).put(id,store);
        }
        // return, either the previous value, or the one we just put there
        return factory.get(type).get(id);
    }    
}
