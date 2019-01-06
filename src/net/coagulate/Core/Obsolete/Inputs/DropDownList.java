package net.coagulate.Core.HTML.Inputs;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.coagulate.Core.HTML.Outputs.Renderable;

/** A drop down list choice.
 *
 * @author Iain Price <gphud@predestined.net>
 */
public class DropDownList extends Input {

    String name;
    public DropDownList(String name) { this.name=name; add(""); }
    
    Map<String,String> choices=new TreeMap<>();

    public void add(String choice) { choices.put(choice,choice); }
    public void add(String choice,String label) { choices.put(choice,label); }
    
    @Override
    public String asHtml() {
        String r="";
        r+="<select name=\""+name+"\">";
        for (String option:choices.keySet()) {
            r+="<option value=\""+option+"\"";
            if (option.equalsIgnoreCase(value)) { r+=" selected"; }
            r+=">"+choices.get(option)+"</option>";
        }
        r+="</select>";
        return r;
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }
    
}
