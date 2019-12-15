package net.coagulate.Core.Database;

import javax.annotation.Nonnull;

/**
 * @author Iain Price
 */
public abstract class StandardTableRow extends TableRow {

	public StandardTableRow(final int id) { super(id); }

	@Nonnull
	@Override
	public String getIdColumn() { return "id"; }

}
