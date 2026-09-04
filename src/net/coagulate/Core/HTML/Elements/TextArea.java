package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class TextArea extends TagPair {
	public TextArea(final String name) {
		this(name,"");
	}

	public TextArea(final String name,final String content) {
		super(new PlainText(content));
		replaceAttribute("id",name);
		replaceAttribute("name",name);
	}

	@Override
	public String tag() {
		return "textarea";
	}
	
}
