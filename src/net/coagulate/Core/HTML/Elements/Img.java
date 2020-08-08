package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.SingleTag;

import javax.annotation.Nonnull;

public class Img extends SingleTag {
    @Override
    public String tag() {
        return "img";
    }
    public Img(@Nonnull final String src) {
        addTag("src",src);
    }
}
