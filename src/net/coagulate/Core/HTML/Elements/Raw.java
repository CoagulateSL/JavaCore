package net.coagulate.Core.HTML.Elements;

import net.coagulate.Core.HTML.Container;

import javax.annotation.Nonnull;
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

    public void toString(@Nonnull final StringBuilder sb) {
        sb.append(text);
    }
}
