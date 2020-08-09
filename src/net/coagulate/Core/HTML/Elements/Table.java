package net.coagulate.Core.HTML.Elements;

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
        TableRow row=new TableRow();
        add(row);
        return row;
    }
}
