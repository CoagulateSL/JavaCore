package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Preformatted extends TagPair {
    public Preformatted(String input) {
        super(input);
    }

    public Preformatted() {
        super();
    }

    @Override
    public String tag() {
        return "pre";
    }
}
