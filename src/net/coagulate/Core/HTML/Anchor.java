package net.coagulate.Core.HTML;

/**
 *
 * @author Iain Price
 */
public class Anchor extends Container {
    String target;
    public Anchor(String target) { this.target=target; }
    public Anchor(String target,Element content) { this.target=target; add(content); }
    public Anchor(String target,String content) { this.target=target; add(new Raw(content)); }

    public String toHtml() {
        return 
                "<a href=\"#"+target+"\">"+
                super.toHtml()+
                "</a>";
    }
}
