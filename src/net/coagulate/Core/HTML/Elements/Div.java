package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class Div extends TagPair {

	public Div() {
	}

	public Div(final Container container) {
		super(container);
	}

	public Div(final String textcontent) {
		super(textcontent);
	}

	@Override
	public String tag() {
		return "div";
	}
}
