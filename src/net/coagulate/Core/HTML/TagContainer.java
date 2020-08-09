package net.coagulate.Core.HTML;

import net.coagulate.Core.Exceptions.System.SystemImplementationException;

public abstract class TagContainer extends Container {

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
        StringBuilder tag=new StringBuilder("<");
        tag.append(tag());
        String attributes=tagAttributes();
        if (attributes!=null && !attributes.isBlank()) { tag.append(" ").append(attributes); }
        tag.append(">");
        return tag.toString();
    }
    private String closeTag() {
        return "</"+tag()+">";
    }

    @Override
    public Container add(Container content) {
        if (!container()) { throw new SystemImplementationException("Adding content to a non container tag!"); }
        return super.add(content);
    }

}
