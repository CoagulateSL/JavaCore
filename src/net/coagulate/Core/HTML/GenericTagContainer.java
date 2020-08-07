package net.coagulate.Core.HTML;

import net.coagulate.Core.Exceptions.System.SystemImplementationException;

public abstract class GenericTagContainer extends LinearContainer {

    /** The HTML tag/element name
     *
     * @return The HTML tag/element name
     */
    public abstract String tag();
    public abstract boolean container();
    @Override
    public String toHTML() {
        if (container()) { return openTag()+super.toHTML()+closeTag(); }
        return openTag();
    }
    private String openTag() {
        StringBuilder tag=new StringBuilder("<");
        tag.append(tag());
        tag.append(">");
        return tag.toString();
    }
    private String closeTag() {
        return "</"+tag()+">";
    }

    @Override
    public void add(Container content) {
        if (!container()) { throw new SystemImplementationException("Adding content to a non container tag!"); }
        super.add(content);
    }
}
