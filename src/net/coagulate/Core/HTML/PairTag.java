package net.coagulate.Core.HTML;

public abstract class PairTag extends AttributeMapTag {
    @Override
    public boolean container() {
        return true;
    }
}
