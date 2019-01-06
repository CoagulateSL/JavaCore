package net.coagulate.Core.HTML;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Iain Price
 */
public class Table extends Container {
    // a table is ...
    List<List<Element>> table;
    List<Element> headers=new ArrayList<>();
    List<Element> row=null;
    
    public Table() { 
        table=new ArrayList<>();
    }
    public Table openRow() {
        row=new ArrayList<>();
        table.add(row);
        return this;
    }
    public Table checkRow() { if (row==null) { openRow(); } return this; }
    public Table add(Element e) { checkRow(); row.add(e); return this; }
    public Table add(String s) { checkRow(); row.add(new Raw(s)); return this; }
    public Table header(Element e) { headers.add(e); return this; }
    public Table header(String s) { headers.add(new Raw(s)); return this; }
    
    public void load(Map map) {
        for (List<Element> list:table) {
            for (Element ele:list) {
                ele.load(map);
            }
        }
    }
    public String toHtml() {
        return 
                "<table>"+
                headerRow()+
                contentRows()+
                "</table>";
    }
    protected String headerRow() {
        if (headers.isEmpty()) { return ""; }
        String r="<tr>";
        for (Element e:headers) {
            r=r+
                    "<th>"+
                    e.toHtml()+
                    "</th>";
        }
        r+="</tr>";
        return r;
    }
    protected String contentRows() {
        String r="";
        for (List<Element> row:table) {
            r+="<tr>";
            for (Element cell:row) {
                r+="<td>";
                r+=cell.toHtml();
                r+="</td>";
            }
            r+="</tr>";
        }
        return r;
    }
    
}
