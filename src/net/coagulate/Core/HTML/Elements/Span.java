package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class Span extends TagPair {

	public Span() {
	}

	public Span(final Container container) {
		super(container);
	}

	public Span(final String spanName) {
		replaceAttribute("id",spanName);
	}

	@Override
	public String tag() {
		return "span";
	}
}
