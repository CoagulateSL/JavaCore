package net.coagulate.Core.Database;

/**
 *
 * @author Iain Price
 */
public interface Table {
    public abstract DBConnection getDatabase();
    public abstract String getTableName();
}
