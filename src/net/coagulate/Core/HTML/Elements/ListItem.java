package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class ListItem extends TagPair {

    public ListItem(final Container container) {
        super(container);
    }

    public ListItem() {
    }

    public ListItem(final String text) {
        super(text);
    }

    @Override
    public String tag() {
        return "li";
    }
}
