package net.coagulate.Core.HTML;

public abstract class TagPair extends TagContainer {
    public TagPair(final Container container) {
        super(container);
    }

    @Override
    public boolean container() {
        return true;
    }

    public TagPair() {
    }

    public TagPair(final String text) {
        super(text);
    }
}
