package net.coagulate.Core.HTML.Outputs;

/** A row in a table that is of header type e.g. <th>.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class HeaderRow extends TRow {

    public HeaderRow() {super();}
    public HeaderRow(Cell c) {
        super(c);
    }
    public HeaderRow(String c) {
        super(c);
    }
    @Override
    public boolean isHeader() { return true; }
    
}
