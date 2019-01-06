package net.coagulate.Core.HTML;

/**
 *
 * @author Iain Price
 */
public class Form extends Container{
    public String toHtml() {
        return
                "<form method=post>"+
                super.toHtml()+
                "</form>";
    }

    public Form submit(String s) { add(new Submit(s)); return this; }
    public Form submit(Element e) { add(e); return this; }
}
