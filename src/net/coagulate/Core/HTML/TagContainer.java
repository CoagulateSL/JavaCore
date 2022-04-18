package net.coagulate.Core.HTML;

import net.coagulate.Core.Exceptions.System.SystemImplementationException;
import net.coagulate.Core.HTML.Elements.PlainText;

public abstract class TagContainer extends Container {

    protected TagContainer() {
    }

    protected TagContainer(final String textcontent) {
        add(new PlainText(textcontent));
    }

    protected TagContainer(final Container container) {
        add(container);
    }

    /** The HTML tag/element name
     *
     * @return The HTML tag/element name
     */
    public abstract String tag();
    public abstract boolean container();
    @Override
    public String toString() {
        if (container()) { return openTag()+super.toString()+closeTag(); }
        return openTag();
    }
    private String openTag() {
        final StringBuilder tag = new StringBuilder("<");
        tag.append(tag());
        final String attributes = tagAttributes();
        if (attributes!=null && !attributes.isBlank()) { tag.append(" ").append(attributes); }
        tag.append(">");
        return tag.toString();
    }
    private String closeTag() {
        return "</"+tag()+">";
    }

    @Override
    public Container add(final Container content) {
        if (!container()) {
            throw new SystemImplementationException("Adding content to a non container tag!");
        }
        return super.add(content);
    }

    public TagContainer size(final int size) {
        replaceAttribute("size", String.valueOf(size));
        return this;
    }

    public TagContainer autofocus() { addAttribute("autofocus",null); return this; }
}
