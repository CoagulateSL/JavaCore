package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class Button extends TagPair {
    public Button(Container container) {
        super(container);
    }

    public Button() {
        super();
    }

    public Button(String type) {
        super();
        replaceAttribute("type",type);
    }

    @Override
    public String tag() {
        return "button";
    }
}
