package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.TagPair;

public class Preformatted extends TagPair {
	public Preformatted(final String textcontent) {
		super(textcontent);
	}
	
	public Preformatted() {
	}
	
	@Override
	public String tag() {
		return "pre";
	}
}
