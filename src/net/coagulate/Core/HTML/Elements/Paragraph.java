package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class Paragraph extends TagPair {
    public Paragraph(final Container container) {
        super(container);
    }

    public Paragraph() {
    }

    public Paragraph(final String textcontent) {
        super(textcontent);
    }

    @Override
    public String tag() {
        return "p";
    }
}
