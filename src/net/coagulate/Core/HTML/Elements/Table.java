package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.Exceptions.System.SystemImplementationException;
import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class Table extends TagPair {
    @Override
    public String tag() {
        return "table";
    }

    public Table border() {
        addAttribute("border","1");
        return this;
    }

    public Table collapsedBorder() {
        border();
        addAttribute("style","border-collapse: collapse;");
        return this;
    }

    public TableRow row() {
        final TableRow row = new TableRow();
        contents().add(row);
        return row;
    }

    @Override
    public Container add(final Container content) {
        throw new SystemImplementationException("You can not add content directly to a table object (you need a row)");
    }

    @Override
    public Container add(final String content) {
        throw new SystemImplementationException("You can not add content directly to a table object (you need a row)");
    }
}
