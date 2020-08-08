package net.coagulate.Core.HTML;

public abstract class SingleTag extends AttributeMapTag {
    @Override
    public boolean container() {
        return false;
    }
}
