package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Header3 extends TagPair {
    @Override
    public String tag() {
        return "h3";
    }

    public Header3() {
    }

    public Header3(final String text) {
        super(text);
    }
}
