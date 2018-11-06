package net.coagulate.Core.HTML.Outputs;

import java.util.Set;

/** Raw message thats an OK markup.
 * @author Iain Price <gphud@predestined.net>
 */
public class TextOK implements Renderable {
    String s;
    public TextOK(String s) { this.s=s; }

    @Override
    public String asHtml() {
        return "<font color=green>OK : "+s+"</font>";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        return null;
    }
}
