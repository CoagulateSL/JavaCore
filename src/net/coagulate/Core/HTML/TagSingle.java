package net.coagulate.Core.HTML;

public abstract class TagSingle extends TagContainer {
    protected TagSingle() {
    }

    protected TagSingle(final String textcontent) {
        super(textcontent);
    }

    protected TagSingle(final Container container) {
        super(container);
    }

    @Override
    public boolean container() {
        return false;
    }
}
