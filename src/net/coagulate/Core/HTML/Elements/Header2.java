package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Header2 extends TagPair {
    @Override
    public String tag() {
        return "h2";
    }

    public Header2() {
    }

    public Header2(final String textcontent) {
        super(textcontent);
    }
}
