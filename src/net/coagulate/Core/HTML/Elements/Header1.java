package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Header1 extends TagPair {
	public Header1() {
	}
	
	public Header1(final String textcontent) {
		super(textcontent);
	}
	
	@Override
	public String tag() {
		return "h1";
	}
}
