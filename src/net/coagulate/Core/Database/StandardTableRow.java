package net.coagulate.Core.Database;

/**
 *
 * @author Iain Price
 */
public abstract class StandardTableRow extends TableRow {

    @Override
    public final String getIdColumn() { return "id"; }
    
    private final int id;
    
    public final int getId() { return id; }
    
    public StandardTableRow(int id) { this.id=id; }

    
}
