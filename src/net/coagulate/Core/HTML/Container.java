package net.coagulate.Core.HTML;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Iain Price
 */
public class Container implements Element {

    List<Element> content=new ArrayList<>();
    
    @Override
    public String toHtml() {
        String ret="";
        for (Element e:content) { ret+=e.toHtml(); }
        return ret;
    }

    @Override
    public void load(Map<String, String> map) {
        for (Element e:content) { e.load(map); }
    }

    public Container add(Element element) { content.add(element); return this; }
}
