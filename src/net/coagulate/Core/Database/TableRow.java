package net.coagulate.Core.Database;

/**
 *
 * @author Iain Price
 */
public interface TableRow extends Table {
    public abstract String getIdColumn();
    public abstract int getId();
}
