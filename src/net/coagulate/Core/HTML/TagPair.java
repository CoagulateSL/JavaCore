package net.coagulate.Core.HTML;

public abstract class TagPair extends TagContainer {
    public TagPair(Container container) { super(container); }

    @Override
    public boolean container() {
        return true;
    }
    public TagPair() { super(); }
    public TagPair(String text) { super(text); }
}
