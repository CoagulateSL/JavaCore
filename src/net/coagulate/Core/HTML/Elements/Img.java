package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagSingle;

import javax.annotation.Nonnull;

public class Img extends TagSingle {
	public Img(@Nonnull final String src) {
		addAttribute("src",src);
	}
	
	@Override
	public String tag() {
		return "img";
	}
}
