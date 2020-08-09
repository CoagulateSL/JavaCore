package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagSingle;

import javax.annotation.Nonnull;

public class Img extends TagSingle {
    @Override
    public String tag() {
        return "img";
    }
    public Img(@Nonnull final String src) {
        addAttribute("src",src);
    }
}
