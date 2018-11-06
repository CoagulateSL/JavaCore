package net.coagulate.Core.HTML.Outputs;

import java.util.Set;

/** Just a hr tag.
 * @author Iain Price <gphud@predestined.net>
 */
public class Separator implements Renderable {

    @Override
    public String asHtml() {
        return "<hr>";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        return null;
    }

}
