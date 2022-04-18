package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class TableData extends TagPair {
    public TableData(final String data) {
        add(new PlainText(data));
    }

    public TableData() {
    }

    @Override
    public String tag() {
        return "td";
    }

    public void span(final int span) {
        addAttribute("colspan", String.valueOf(span));
    }
}
