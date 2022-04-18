package net.coagulate.Core.HTML;

public abstract class TagPair extends TagContainer {
    protected TagPair(final Container container) {
        super(container);
    }

    @Override
    public boolean container() {
        return true;
    }

    protected TagPair() {
    }

    protected TagPair(final String text) {
        super(text);
    }
}
