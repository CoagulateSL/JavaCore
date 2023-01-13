package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Header5 extends TagPair {
	public Header5() {
	}
	
	public Header5(final String textcontent) {
		super(textcontent);
	}
	
	@Override
	public String tag() {
		return "h5";
	}
}
