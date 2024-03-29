package net.coagulate.Core.HTML;

import net.coagulate.Core.Exceptions.System.SystemImplementationException;
import net.coagulate.Core.HTML.Elements.PlainText;

import javax.annotation.Nonnull;

public abstract class TagContainer extends Container {
	
	protected TagContainer() {
	}
	
	protected TagContainer(final String textcontent) {
		add(new PlainText(textcontent));
	}
	
	public abstract boolean container();
	
	protected TagContainer(final Container container) {
		add(container);
	}
	
	@Override
	public void toString(@Nonnull final StringBuilder sb) {
		sb.append(openTag());
		if (container()) {
			super.toString(sb);
			sb.append(closeTag());
		}
	}
	
	private String openTag() {
		final StringBuilder tag=new StringBuilder("<");
		tag.append(tag());
		final String attributes=tagAttributes();
		if (attributes!=null&&!attributes.isBlank()) {
			tag.append(" ").append(attributes);
		}
		tag.append(">");
		return tag.toString();
	}
	
	private String closeTag() {
		return "</"+tag()+">";
	}
	
	/**
	 * The HTML tag/element name
	 *
	 * @return The HTML tag/element name
	 */
	public abstract String tag();
	
	@Override
	public Container add(final Container content) {
		if (!container()) {
			throw new SystemImplementationException("Adding content to a non container tag!");
		}
		return super.add(content);
	}
	
	public TagContainer size(final int size) {
		replaceAttribute("size",String.valueOf(size));
		return this;
	}
	
	public TagContainer autofocus() {
		addAttribute("autofocus",null);
		return this;
	}
}
