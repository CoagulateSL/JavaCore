package net.coagulate.Core.Tools;

import org.apache.http.Header;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public abstract class DumpableState {


	public String toHTML() {
		String ret = "<table>";
		for (Field f : this.getClass().getDeclaredFields()) {
			ret += "<tr><th valign=top>" + f.getName() + "</th><td valign=top>";
			try {
                /*if (!f.canAccess(this)) {
                    f.setAccessible(true);
                }*/
				f.setAccessible(true);
				Object content = f.get(this);
				ret += toHTML(content);
			} catch (IllegalArgumentException ex) {
				ret += "IllegalArgument";
			} catch (IllegalAccessException ex) {
				ret += "IllegalAccess";
			}
			ret += "</td></tr>";
		}
		ret += "</table>";
		ret+=dumpAdditionalStateToHtml();
		return ret;
	}

	protected abstract String dumpAdditionalStateToHtml();

	private String toHTML(Object o) {
		if (o == null) { return "</td><td valign=top><i>NULL</i>"; }
		String ret = o.getClass().getSimpleName() + "</td><td valign=top>";
		boolean handled = false;
		if (o instanceof Header[]) {
			ret += "<table>";
			handled = true;
			for (Header h : ((Header[]) o)) {
				ret += "<tr><td valign=top>" + h.getName() + "</td><td valign=top>" + h.getValue() + "</td></tr>";
			}
			ret += "</table>";
		}
		if (o instanceof TreeMap) {
			handled = true;
			ret += "<table border=1>";
			@SuppressWarnings("unchecked") TreeMap<Object, Object> map = (TreeMap<Object, Object>) o;
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				ret += "<tr><td valign=top>" + toHTML(entry.getKey()) + "</td>";
				ret += "<td valign=top>" + toHTML(entry.getValue()) + "</td></tr>";
			}
			ret += "</table>";
		}
		if (!handled) { ret += o.toString(); }
		return ret;
	}

}
