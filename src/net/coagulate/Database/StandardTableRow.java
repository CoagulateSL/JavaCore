package net.coagulate.Database;

/**
 *
 * @author Iain Price
 */
public abstract class StandardTableRow implements TableRow {

    @Override
    public String getIdColumn() { return "id"; }

    
}
