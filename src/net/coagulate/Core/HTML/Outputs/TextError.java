package net.coagulate.Core.HTML.Outputs;

import java.util.Set;

/** Raw message thats an error markup.
 * @author Iain Price <gphud@predestined.net>
 */
public class TextError implements Renderable {
    String s;
    public TextError(String s) { this.s=s; }

    @Override
    public String asHtml() {
        return "<font color=red><b> *** ERROR : "+s+" *** </b></font>";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        return null;
    }
}
