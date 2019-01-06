package net.coagulate.Core.HTML.Outputs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Implements a tabular layout.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class Table implements Renderable {
    List<TRow> table=new ArrayList<>();
    boolean border=false;
    private boolean nowrap=false;
    public void border(boolean border) { this.border=border; }
    
    TRow openrow=null;
    
    public Table openRow() {
        if (openrow!=null) { closeRow(); }
        openrow=new TRow();
        add(openrow);
        return this;
    }
    public Table closeRow() {
        openrow=null;
        return this;
    }
    public Table add(TRow r) { table.add(r); openrow=r; return this; }
    public Table add(String s) { add(new Text(s)); return this; }
    public Table add(Boolean b) { return add(b.toString()); }
    public Table add(Renderable e) { add(new Cell(e)); return this; }
    public Table add(Cell e) {
        if (openrow==null) { openRow(); }
        openrow.add(e);
        return this;
    }

    @Override
    public String asHtml() {
        String s="";
        s+="<table";
        if (border) { s+=" border=1"; }
        if (nowrap) { s+=" style=\"white-space: nowrap;\""; }
        s+=">";
        for (TRow r:table) { s+=r.asHtml(); }
        s+="</table>";
        return s;
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        Set<Renderable> r=new HashSet<>();
        for (TRow row:table) { r.add(row); }
        return r;
    }

    public void addNoNull(Renderable addable) {
        if (addable==null) { add(""); } else { add(addable); }
    }
    public void addNoNull(String addable) {
        if (addable==null) { add(""); } else { add(addable); }
    }    

    public int rowCount() {
        return table.size();
    }

    public void nowrap() { nowrap=true; }
}
