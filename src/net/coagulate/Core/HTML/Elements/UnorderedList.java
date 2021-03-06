package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class UnorderedList extends TagPair {
    @Override
    public String tag() {
        return "ul";
    }

    public Container add(String s) {
        add(new ListItem(s));
        return this;
    }
}
