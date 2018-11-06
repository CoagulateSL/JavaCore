package net.coagulate.Core.HTML.Inputs;

import net.coagulate.Core.HTML.Outputs.Renderable;

/** For elements that read input (in HTML).
 *
 * @author Iain Price <gphud@predestined.net>
 */
public abstract class Input implements Renderable {
    String value="";
    public abstract String getName();
    public void setValue(String value) { this.value=value; } 
    private String getValue() { return value; }

}
 