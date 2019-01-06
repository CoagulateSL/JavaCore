package net.coagulate.Core.HTML.Outputs;

import java.util.HashSet;
import java.util.Set;

/** Paragraph.
 * @author Iain Price <gphud@predestined.net>
 */
public class Paragraph implements Renderable {

    Renderable content;
    public Paragraph(String s) { content=new Text(s); }
    public Paragraph(Renderable r) { content=r; }

    public Paragraph() {
        content=new Text("");
    }

    @Override
    public String asHtml() {
        return "<p>"+content.asHtml()+"</p>";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        Set<Renderable> r=new HashSet<>();
        r.add(content);
        return r;
    }
}
