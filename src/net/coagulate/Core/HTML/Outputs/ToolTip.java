package net.coagulate.Core.HTML.Outputs;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Iain Price
 */
public class ToolTip implements Renderable {

    private final String element;
    private final Renderable tooltip;

    public ToolTip(String element,String tooltip) { this.element=element; this.tooltip=new Text(tooltip); }

    public ToolTip(String element, Renderable tooltip) { this.element=element; this.tooltip=tooltip; }

    @Override
    public String asHtml() {
        return "<div class=\"tooltip\">"+element+"<span class=\"tooltiptext\">"+tooltip.asHtml()+"</span></div>";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        return new HashSet<>();
    }
    
}
