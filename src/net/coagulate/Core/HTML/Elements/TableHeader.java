package net.coagulate.Core.HTML.Elements;

public class TableHeader extends TableData {

    public TableHeader(String name) { super(name); }

    @Override
    public String tag() {
        return "th";
    }
}
