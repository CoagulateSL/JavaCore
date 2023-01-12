package net.coagulate.Core.HTML;

public abstract class TagPair extends TagContainer {
	protected TagPair(final Container container) {
		super(container);
	}
	
	protected TagPair() {
	}
	
	protected TagPair(final String textcontent) {
		super(textcontent);
	}
	
	@Override
	public boolean container() {
		return true;
	}
}
