package net.coagulate.Core.HTML;

public abstract class SingleTag extends GenericTagContainer {
    @Override
    public boolean container() {
        return false;
    }
}
