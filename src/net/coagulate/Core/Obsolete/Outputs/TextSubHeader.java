package net.coagulate.Core.HTML.Outputs;

import java.util.HashSet;
import java.util.Set;

/** Implements a header element.
 * Hmm, this is bad, the content should just be an element, not forced text.  
 * @author Iain Price <gphud@predestined.net>
 */
public class TextSubHeader implements Renderable {

    Renderable content;
    public TextSubHeader(String s) { content=new Text(s); }
    public TextSubHeader(Renderable r) { content=r; }

    @Override
    public String asHtml() {
        return "<h3>"+content.asHtml()+"</h3>";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        Set<Renderable> r=new HashSet<>();
        r.add(content);
        return r;
    }
}
