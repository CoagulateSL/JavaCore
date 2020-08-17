package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class TableRow extends TagPair {
    @Override
    public String tag() {
        return "tr";
    }

    public TableRow header(String name) {
        contents().add(new TableHeader(name));
        return this;
    }

    public TableRow data(String name) {
        contents().add(new TableData(name));
        return this;
    }

    public TableRow data(Container content) { contents().add(new TableData().add(content)); return this; }
    public TableRow header(Container content) { contents().add(new TableHeader().add(content)); return this; }

    public TableRow alignCell(String alignment) {
        contents().get(contents().size()-1).alignment(alignment);
        return this;
    }

    public TableRow spanCell(int span) {
        ((TableData)(contents().get(contents().size()-1))).span(span);
        return this;
    }
    @Override
    public TableRow add(Container content) {
        data(content);
        return this;
    }
    @Override
    public Container add(String content) {
        data(content);
        return this;
    }

}
