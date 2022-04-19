package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class TableRow extends TagPair {
    @Override
    public String tag() {
        return "tr";
    }

    public TableRow header(final String name) {
        contents().add(new TableHeader(name));
        return this;
    }

    public TableRow data(final String name) {
        contents().add(new TableData(name));
        return this;
    }

    public TableRow data(final Container content) {
        contents().add(new TableData().add(content));
        return this;
    }

    public TableRow header(final Container content) {
        contents().add(new TableHeader().add(content));
        return this;
    }

    public TableRow alignCell(final String alignment) {
        contents().get(contents().size() - 1).alignment(alignment);
        return this;
    }

    public TableRow spanCell(final int span) {
        ((TableData) (contents().get(contents().size() - 1))).span(span);
        return this;
    }

    @Override
    public TableRow add(final Container content) {
        data(content);
        return this;
    }

    @Override
    public Container add(final String text) {
        data(text);
        return this;
    }

}
