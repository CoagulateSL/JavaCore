package net.coagulate.Core.Database;

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
}
