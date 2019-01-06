package net.coagulate.Core.HTML.Outputs;

import java.util.HashSet;
import java.util.Set;
import net.coagulate.Core.Tools.SystemException;

/** A cell in a table.
 * Basically an encapsulated Element, that is an element its self.
 * @author iain
 */
public class Cell implements Renderable {

    Renderable e;
    boolean header=false;
    int colspan=1;
    String align="";
    public Cell() {}
    public Cell(String s) { e=new Text(s); }
    public Cell(Renderable e) { if (e==null) { throw new SystemException("Abstract Cell is not renderable."); } this.e=e; }
    public Cell(String s,int colspan) { e=new Text(s); this.colspan=colspan; }
    public Cell(Renderable e,int colspan) { if (e==null) { throw new SystemException("Abstract Cell is not renderable"); }  this.e=e; this.colspan=colspan; }

    @Override
    public String asHtml() {
        String s="";
        if (header) { s+="<th"; } else { s+="<td"; }
        if (colspan>1) { s+=" colspan="+colspan; }
        if (!align.isEmpty()) { s+=" align="+align; }
        s+=">";
        s+=e.asHtml();
        s+="</";
        if (header) { s+="th>"; } else { s+="td>"; }
        return s;                
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        Set<Renderable> r=new HashSet<>();
        r.add(e);
        return r;
    }

    public Cell th() {
        header=true;
        return this;
    }

    public Cell align(String align) {
        this.align=align;
        return this;
    }
    
}
