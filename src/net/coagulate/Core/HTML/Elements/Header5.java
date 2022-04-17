package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Header5 extends TagPair {
    @Override
    public String tag() {
        return "h5";
    }

    public Header5() {
    }

    public Header5(final String text) {
        super(text);
    }
}
