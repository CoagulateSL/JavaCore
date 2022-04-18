package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class UnorderedList extends TagPair {
    @Override
    public String tag() {
        return "ul";
    }

    public Container add(final String text) {
        add(new ListItem(text));
        return this;
    }
}
