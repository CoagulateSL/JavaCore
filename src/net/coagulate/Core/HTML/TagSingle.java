package net.coagulate.Core.HTML;

public abstract class TagSingle extends TagContainer {
    @Override
    public boolean container() {
        return false;
    }
}
