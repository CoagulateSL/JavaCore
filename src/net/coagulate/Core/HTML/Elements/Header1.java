package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Header1 extends TagPair {
    @Override
    public String tag() {
        return "h1";
    }

    public Header1() { super(); }

    public Header1(String text) { super(text); }
}
