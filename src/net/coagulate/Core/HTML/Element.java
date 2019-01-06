package net.coagulate.Core.HTML;

import java.util.Map;

/**
 *
 * @author Iain Price
 */
public interface Element {
    
    /** Render this element (and any children inline) */
    public String toHtml();
    /** Load key values from the map, for input elements */
    public void load(Map<String,String> map);
    
}
