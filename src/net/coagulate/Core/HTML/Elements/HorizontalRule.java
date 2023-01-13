package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;
import net.coagulate.Core.HTML.TagSingle;

public class HorizontalRule extends TagSingle {
	public HorizontalRule(final Container container) {
		super(container);
	}
	
	public HorizontalRule() {
	}
	
	public HorizontalRule(final String textcontent) {
		super(textcontent);
	}
	
	@Override
	public String tag() {
		return "hr";
	}
}
