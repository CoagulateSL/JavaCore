package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class TableData extends TagPair {
    public TableData(String data) { add(new PlainText(data)); }
    @Override
    public String tag() {
        return "td";
    }

    public void span(int span) {
        addAttribute("colspan",span+"");
    }
}
