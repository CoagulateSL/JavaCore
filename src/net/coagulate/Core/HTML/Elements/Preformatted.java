package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Preformatted extends TagPair {
    public Preformatted(final String input) {
        super(input);
    }

    public Preformatted() {
    }

    @Override
    public String tag() {
        return "pre";
    }
}
