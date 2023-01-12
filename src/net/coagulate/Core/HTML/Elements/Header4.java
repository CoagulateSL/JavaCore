package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Header4 extends TagPair {
	public Header4() {
	}
	
	public Header4(final String textcontent) {
		super(textcontent);
	}
	
	@Override
	public String tag() {
		return "h4";
	}
}
