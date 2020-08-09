package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class TableRow extends TagPair {
    @Override
    public String tag() {
        return "tr";
    }

    public TableRow header(String name) {
        add(new TableHeader(name));
        return this;
    }

    public TableRow data(String name) {
        add(new TableData(name));
        return this;
    }

    public TableRow alignCell(String alignment) {
        contents().get(contents().size()-1).alignment(alignment);
        return this;
    }

    public void spanCell(int span) {
        ((TableData)(contents().get(contents().size()-1))).span(span);
    }
}
