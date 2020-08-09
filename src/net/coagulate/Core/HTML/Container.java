package net.coagulate.Core.HTML;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Container {

    private final List<Container> contents=new ArrayList<>();
    protected final List<Container> contents() { return contents; }
    @Override
    public String toString() {
        StringBuilder result=new StringBuilder();
        for (Container content:contents()) {
            result.append(content.toString());
        }
        return result.toString();
    }

    public void load(Map<String, String> parameters) {
        for (Container content:contents()) { content.load(parameters); }
    }

    public Container add(Container content) { contents().add(content); return this; }

    // not everything uses attributes but it makes life so much easier to have it 'general' for cascading etc
    private final Map<String,String> attributes=new HashMap<>();

    public String tagAttributes() {
        StringBuilder attributelist=new StringBuilder();
        boolean addedanything=false;
        for(Map.Entry<String,String> tag:attributes.entrySet()) {
            if (addedanything) { attributelist.append(" "); }
            attributelist.append(tag.getKey());
            if (tag.getValue()!=null) {
                attributelist.append("=\"");
                attributelist.append(tag.getValue());
                attributelist.append("\"");
            }
            addedanything=true;
        }
        return attributelist.toString();
    }
    protected void addAttribute(@Nonnull String name, String value) {
        if (attributes.containsKey(name)) { value=attributes.get(name)+" "+value; }
        attributes.put(name,value);
    }
    protected void replaceAttribute(@Nonnull String name, String value) {
        attributes.put(name,value);
    }

    public void styleCascade(String s) {
        for (Container content:contents()) {
            addAttribute("style",s);
            content.styleCascade(s);
        }
    }

    public void alignment(String alignment) {
        replaceAttribute("align",alignment);
    }
    // don't forget to overwrite toString
}
