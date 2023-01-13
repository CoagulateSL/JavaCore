package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagPair;

public class Anchor extends TagPair {
	public Anchor(final Container container) {
		super(container);
	}
	
	public Anchor() {
	}
	
	public Anchor(final String href) {
		href(href);
	}
	
	public Anchor href(final String url) {
		replaceAttribute("href",url);
		return this;
	}
	
	public Anchor(final String href,final String content) {
		href(href);
		add(content);
	}
	
	@Override
	public String tag() {
		return "a";
	}
	
	
}
