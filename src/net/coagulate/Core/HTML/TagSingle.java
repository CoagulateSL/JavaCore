package net.coagulate.Core.HTML;

public abstract class TagSingle extends TagContainer {
    public TagSingle() { super(); }

    public TagSingle(String textcontent) {
        super(textcontent);
    }

    public TagSingle(Container container) {
        super(container);
    }

    @Override
    public boolean container() {
        return false;
    }
}
