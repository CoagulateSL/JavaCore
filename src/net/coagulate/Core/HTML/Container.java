package net.coagulate.Core.HTML;

import net.coagulate.Core.HTML.Elements.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Container {

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
    public Container add(String text) { contents.add(new PlainText(text)); return this; }

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
    public void replaceAttribute(@Nonnull String name, String value) {
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

    public Form form() { Form f=new Form(); add(f); return f; }

    public Table table() { Table t=new Table(); add(t); return t; }
    // don't forget to overwrite toString
    public Container p(String paragraph) { add(new Paragraph().add(new PlainText(paragraph))); return this; }
    public Paragraph p() { Paragraph p=new Paragraph(); add(p); return p; }

    public Anchor a() { Anchor a=new Anchor(); add(a); return a; }
    public Container a(String url, String label) { add(new Anchor(url,label)); return this; }

    public Container hr() { add(new HorizontalRule()); return this; }

    /** Find an element by "name" tag
     *
     * @param name Name to search
     * @return The container with that name, or null if not found
     */
    @Nullable
    public Container findByName(String name) {
        // is it us?
        if (attributes.containsKey("name")) {
            if (attributes.get("name").equalsIgnoreCase(name)) {
                return this;
            }
        }
        // no? ask our children
        for (Container content:contents()) {
            Container match=content.findByName(name);
            if (match!=null) { return match; }
        }
        // not in this part of the tree then
        return null;
    }
    @Nonnull
    public String getAttribute(String key,@Nonnull String defaultvalue) {
        String result=getAttribute(key);
        if (result==null) { return defaultvalue; }
        return result;
    }

    @Nullable
    public String getAttribute(String key) {
        if (attributes.containsKey(key)) { return attributes.get(key); }
        return null;
    }

    public Container header1(String header) { add(new Header1(header)); return this; }
    public Container header2(String header) { add(new Header2(header)); return this; }
    public Container header3(String header) { add(new Header3(header)); return this; }
    public Container header4(String header) { add(new Header4(header)); return this; }
    public Container header5(String header) { add(new Header5(header)); return this; }

    public Container align(String alignment) {
        last().replaceAttribute("align",alignment);
        return this;
    }

    private Container last() {
        return contents.get(contents.size()-1);
    }

    public Container submit(String name) {
        add(new ButtonSubmit(name));
        return this;
    }

    public Container p(Container content) {
        add(new Paragraph(content));
        return this;
    }
}
