package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class Paragraph extends TagPair {
    public Paragraph(Container header1) { super(header1); }

    public Paragraph() { super();
    }

    public Paragraph(String text) {
        super(text);
    }

    @Override
    public String tag() {
        return "p";
    }
}
