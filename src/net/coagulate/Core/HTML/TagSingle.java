package net.coagulate.Core.HTML;

public abstract class TagSingle extends TagContainer {
    public TagSingle() { super(); }

    public TagSingle(final String textcontent) {
        super(textcontent);
    }

    public TagSingle(final Container container) {
        super(container);
    }

    @Override
    public boolean container() {
        return false;
    }
}
