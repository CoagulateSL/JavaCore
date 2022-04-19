package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;

import java.util.Map;
@Deprecated
public class Raw extends Container {

    @Override
    public void load(final Map<String, String> parameters) {
    }

    private final String text;

    public Raw(final String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
