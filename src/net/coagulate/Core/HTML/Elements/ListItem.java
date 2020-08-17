package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class ListItem extends TagPair {

    public ListItem(Container container) {
        super(container);
    }

    public ListItem() { super();  }

    public ListItem(String text) {
        super(text);
    }

    @Override
    public String tag() {
        return "li";
    }
}
