package net.coagulate.Core.HTML;

import java.util.Map;

/**
 *
 * @author Iain Price
 */
public class Raw implements Element {

    private String content;
    public Raw(String content) { this.content=content; }
    
    @Override
    public String toHtml() {
        return content;
    }

    @Override
    public void load(Map<String, String> map) {}
    
}
