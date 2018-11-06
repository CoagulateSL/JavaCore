package net.coagulate.Core.HTML.Inputs;

import java.util.Set;
import net.coagulate.Core.HTML.Outputs.Renderable;

/** Implements a single line text input box.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class CheckBox extends Input {
    String name="";
    public CheckBox(String name) {this.name=name;} 
    public CheckBox(String name,String value) {this.name=name;this.value=value;} 

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String asHtml() {
        String r="<input type=\"checkbox\" name=\""+name+"\" ";
        if (value!=null && !value.isEmpty()) { r+="checked"; }
        r+=" />";
        return r;
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        return null;
    }
}
