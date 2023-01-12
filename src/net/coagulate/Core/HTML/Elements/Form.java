package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Form extends TagPair {
	public Form() {
		addAttribute("method","post");
	}
	
	@Override
	public String tag() {
		return "form";
	}
}
