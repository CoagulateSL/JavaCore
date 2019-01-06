package net.coagulate.Core.HTML.Outputs;

import java.util.HashSet;
import java.util.Set;

/** Implements a header element.
 * Hmm, this is bad, the content should just be an element, not forced text.  
 * @author Iain Price <gphud@predestined.net>
 */
public class TextHeader implements Renderable {
    Renderable content;
    public TextHeader(String s) { content=new Text(s); }
    public TextHeader(Renderable r) { content=r; }


    @Override
    public String asHtml() {
        return "<h1>"+content.asHtml()+"</h1>";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        Set<Renderable> r=new HashSet<>();
        r.add(content);
        return r;
    }
}
