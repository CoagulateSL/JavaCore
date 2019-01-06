package net.coagulate.Core.HTML;

import net.coagulate.Core.HTML.Outputs.Renderable;

/**
 *
 * @author Iain Price
 */
public class HTMLComposer {
    public static void add(Renderable r) {
        Page.get().add(r);
    }
}
