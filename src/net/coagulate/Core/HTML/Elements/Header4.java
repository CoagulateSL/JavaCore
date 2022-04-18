package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Header4 extends TagPair {
    @Override
    public String tag() {
        return "h4";
    }

    public Header4() {
    }

    public Header4(final String textcontent) {
        super(textcontent);
    }
}
