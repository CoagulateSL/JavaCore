package net.coagulate.Core.HTML.Elements;

public class TableHeader extends TableData {

    public TableHeader(final String name) {
        super(name);
    }

    public TableHeader() {
        super();
    }

    @Override
    public String tag() {
        return "th";
    }
}
