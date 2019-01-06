package net.coagulate.Core.HTML.Inputs;

import java.util.Set;
import net.coagulate.Core.HTML.Outputs.Renderable;

/** Hidden input element.
 *
 * @author iain
 */
public class Hidden extends Input {

    String name;
    
    public Hidden(String name,String value) {this.name=name; this.value=value; }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String asHtml() {
        return "<input type=hidden name=\""+getName()+"\" value=\""+value+"\">";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        return null;
    }
    
}
