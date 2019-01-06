package net.coagulate.Core.HTML.Outputs;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author iain
 */
public class Colour implements Renderable {
    Renderable content;
    String colour;
    public Colour(String colour,Renderable content) { this.content=content; this.colour=colour; }
    public Colour(String colour,String content) { this.content=new Text(content); this.colour=colour; }

    @Override
    public String asHtml() {
        return "<font color=\""+colour+"\">"+content.asHtml()+"</font>";
    }

    @Override
    public Set<Renderable> getSubRenderables() {
        Set<Renderable> r=new HashSet<>(); r.add(content); return r;
    }
    
    
}
