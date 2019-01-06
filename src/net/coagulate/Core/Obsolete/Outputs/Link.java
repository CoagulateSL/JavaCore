package net.coagulate.Core.HTML.Outputs;

import java.util.HashSet;
import java.util.Set;

/** Implements a header element.
 * Hmm, this is bad, the content should just be an element, not forced text.  
 * @author Iain Price <gphud@predestined.net>
 */
public class Link implements Renderable {
    Renderable content;
    String target;
    public Link(String label,String target) { content=new Text(label); this.target=target; }
    public Link(Renderable label,String target) { content=label; this.target=target; }

    @Override
    public String asHtml() {
        return "<a href=\""+target+"\">"+content.asHtml()+"</a>";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        Set<Renderable> r=new HashSet<>();
        r.add(content);
        return r;
    }
}
