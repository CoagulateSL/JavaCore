package net.coagulate.Core.HTML;

/**
 *
 * @author Iain Price
 */
public class Header1 extends Container {

    public String name=null;
    public Header1(Element e) { add(e); }
    public Header1(String s) { add(new Raw(s)); }
    public Header1 add(Element e) { super.add(e); return this; }
    public String toHtml() {
        return 
            "<h1"+
            (name==null?"":" name=\""+name+"\"")+
            ">"+
            super.toHtml()+
            "</h1>";
    }

    public Header1 name(String name) { this.name=name; return this; }
}
