package net.coagulate.Core.Tools;

import org.apache.http.Header;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public abstract class DumpableState {


	public String toHTML() {
		StringBuilder ret = new StringBuilder("<table>");
		for (Field f : this.getClass().getDeclaredFields()) {
			ret.append("<tr><th valign=top>").append(f.getName()).append("</th><td valign=top>");
			try {
                /*if (!f.canAccess(this)) {
                    f.setAccessible(true);
                }*/
				f.setAccessible(true);
				Object content = f.get(this);
				ret.append(toHTML(content));
			} catch (IllegalArgumentException ex) {
				ret.append("IllegalArgument");
			} catch (IllegalAccessException ex) {
				ret.append("IllegalAccess");
			}
			ret.append("</td></tr>");
		}
		ret.append("</table>");
		ret.append(dumpAdditionalStateToHtml());
		return ret.toString();
	}

	protected abstract String dumpAdditionalStateToHtml();

	private String toHTML(Object o) {
		if (o == null) { return "</td><td valign=top><i>NULL</i>"; }
		StringBuilder ret = new StringBuilder(o.getClass().getSimpleName() + "</td><td valign=top>");
		boolean handled = false;
		if (o instanceof Header[]) {
			ret.append("<table>");
			handled = true;
			for (Header h : ((Header[]) o)) {
				ret.append("<tr><td valign=top>").append(h.getName()).append("</td><td valign=top>").append(h.getValue()).append("</td></tr>");
			}
			ret.append("</table>");
		}
		if (o instanceof TreeMap) {
			handled = true;
			ret.append("<table border=1>");
			@SuppressWarnings("unchecked") TreeMap<Object, Object> map = (TreeMap<Object, Object>) o;
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				ret.append("<tr><td valign=top>").append(toHTML(entry.getKey())).append("</td>");
				ret.append("<td valign=top>").append(toHTML(entry.getValue())).append("</td></tr>");
			}
			ret.append("</table>");
		}
		if (!handled) { ret.append(o.toString()); }
		return ret.toString();
	}

}
