package net.coagulate.Core.HTML.Outputs;

import java.util.Set;

/** For all things that can appear in output.
 * All the formats of output we need.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public interface Renderable {
    /** Render this element into simple HTML.
     * Used for non admin HTML interfaces like the HUD's web panel and Admin interface.
     * non admin interface sets rich to false - dont link to admin pages for entities etc.
     * @param st
     * @param rich Rich mode
     * @return
     */
    public abstract String asHtml();
    
    public abstract Set<Renderable> getSubRenderables();
}
