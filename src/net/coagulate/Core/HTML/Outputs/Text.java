package net.coagulate.Core.HTML.Outputs;

import java.util.Set;

/** Implements plain text, but not like a 'paragraph', more like in a table cell.
 * @author Iain Price <gphud@predestined.net>
 */
public class Text implements Renderable {
    String content;
    public Text(String s) { content=s; }

    @Override
    public String asHtml() {
        return content;
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        return null;
    }
}
