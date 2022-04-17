package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class Paragraph extends TagPair {
    public Paragraph(final Container header1) {
        super(header1);
    }

    public Paragraph() {
    }

    public Paragraph(final String text) {
        super(text);
    }

    @Override
    public String tag() {
        return "p";
    }
}
