package net.coagulate.Core.Database;

/**
 * @author Iain Price
 */
public abstract class StandardTableRow extends TableRow {

	public StandardTableRow(int id) { super(id); }

	@Override
	public String getIdColumn() { return "id"; }

}
