package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagSingle;

public class HorizontalRule extends TagSingle {
    public HorizontalRule(Container container) {
        super(container);
    }

    public HorizontalRule() {
        super();
    }

    public HorizontalRule(String text) {
        super(text);
    }

    @Override
    public String tag() {
        return "hr";
    }
}
