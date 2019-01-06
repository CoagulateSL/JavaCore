package net.coagulate.Core.HTML;

import java.util.Map;

/**
 *
 * @author Iain Price
 */
public class Submit extends Container {

    String name;
    String value=null;
    public Submit(String name) { this.name=name; }
    public Submit(String name,String value) { this.name=name; this.value=value; }
    @Override
    public String toHtml() {
        String v=value; if (v==null) { v=""; }
        return "<button type=submit name=\""+name+"\" value=\""+v+"\">"+
                super.toHtml()+
                "</button>";
    }

    @Override
    public void load(Map<String, String> map) {
        if (value==null) {
            if (map.containsKey(name)) {
                value=map.get(name);
            }
        }
    }
}