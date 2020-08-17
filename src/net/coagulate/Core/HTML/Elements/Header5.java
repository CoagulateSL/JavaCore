package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Header5 extends TagPair {
    @Override
    public String tag() {
        return "h5";
    }

    public Header5() { super(); }

    public Header5(String text) { super(text); }
}
