package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class Anchor extends TagPair {
    public Anchor(Container container) {
        super(container);
    }

    public Anchor() {
        super();
    }

    public Anchor(String href) {
        href(href);
    }
    public Anchor(String href,String content) {
        href(href);
        add(content);
    }
    public Anchor href(String url) { replaceAttribute("href",url); return this; }

    @Override
    public String tag() {
        return "a";
    }


}
