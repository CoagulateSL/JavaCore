package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagSingle;

public class Input extends TagSingle {
    public Input(final Container container) {
        super(container);
    }

    public Input() {
        super();
    }

    public Input(final String type) {
        replaceAttribute("type", type);
    }

    @Override
    public String tag() {
        return "input";
    }
}
