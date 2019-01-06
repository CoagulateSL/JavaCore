package net.coagulate.Core.HTML;

/**
 *
 * @author Iain Price
 */
public class Paragraph extends Container{
    public Paragraph(){}
    public Paragraph(String s) { add(new Raw(s)); }
    public Paragraph add(Element e) { super.add(e); return this; }
    public Paragraph add(String s) { return add(new Raw(s)); }
    public String toHtml() {
        return
                "<p"+renderAlignment()+">"+
                super.toHtml()+
                "</p>";
    }
    private String renderAlignment() {
        if (alignment==ALIGNMENT.CENTER) { return " align=\"center\""; }
        return "";
    }
    public enum ALIGNMENT {NONE,CENTER};
    private ALIGNMENT alignment=ALIGNMENT.NONE;
    public Paragraph align(ALIGNMENT alignment) { this.alignment=alignment; return this; }
}
