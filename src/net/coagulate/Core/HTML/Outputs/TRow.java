package net.coagulate.Core.HTML.Outputs;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/** Implements a row of elements in a table layout.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class TRow implements Renderable {
    List<Cell> row=new ArrayList<>();
    
    public TRow() {}
    public TRow(Cell c) { add(c); }
    public TRow(String c) { add(c); }
    
    public TRow add(Cell c) { row.add(c); return this; }
    public TRow add(String s) { row.add(new Cell(new Text(s))); return this; }
    public TRow add(Renderable r) { row.add(new Cell(r)); return this; }
    public boolean isHeader() { return false; }

    @Override
    public String asHtml() {
        String s="<tr";
        if (!bgcolor.isEmpty()) { s+=" bgcolor="+bgcolor; }
        if (!alignment.isEmpty()) { s+=" align="+alignment; }
        s+=">";
        for (Cell c:row) {
            c.header=isHeader();
            s=s+c.asHtml();
        }
        return s+"</tr>";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        Set<Renderable> r=new HashSet<>();
        for (Cell c:row) { r.add(c); }
        return r;
    }

    public void add(Integer ownerid) {
        add(""+ownerid);
    }

    public void add(boolean online) {
        add(Boolean.toString(online));
    }
    String bgcolor="";
    public void setbgcolor(String setbgcolor) {
        bgcolor=setbgcolor;
    }
    String alignment="";
    public void align(String alignment) {
        this.alignment=alignment;
    }
    
}
