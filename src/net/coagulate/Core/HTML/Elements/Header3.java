package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Header3 extends TagPair {
	public Header3() {
	}
	
	public Header3(final String textcontent) {
		super(textcontent);
	}
	
	@Override
	public String tag() {
		return "h3";
	}
}
