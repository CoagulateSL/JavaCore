package net.coagulate.Core.Tools;

import org.apache.http.Header;
import org.apache.http.message.BasicHttpRequest;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

public abstract class DumpableState {


	@Nonnull
	public String toHTML() {
		final StringBuilder ret=new StringBuilder("<table>");
		for (final Field f: getClass().getDeclaredFields()) {
			ret.append("<tr><th valign=top>").append(f.getName()).append("</th><td valign=top>");
			try {
                /*if (!f.canAccess(this)) {
                    f.setAccessible(true);
                }*/
				f.setAccessible(true);
				final Object content=f.get(this);
				ret.append(toHTML(content));
			}
			catch (@Nonnull final IllegalArgumentException ex) {
				ret.append("IllegalArgument");
			}
			catch (@Nonnull final IllegalAccessException ex) {
				ret.append("IllegalAccess");
			}
			ret.append("</td></tr>");
		}
		ret.append("</table>");
		ret.append(dumpAdditionalStateToHtml());
		return ret.toString();
	}

	@Nonnull
	protected abstract String dumpAdditionalStateToHtml();

	@Nonnull
	private String toHTML(@Nullable final Object o) {
		if (o==null) { return "</td><td valign=top><i>NULL</i>"; }
		final StringBuilder ret=new StringBuilder(o.getClass().getSimpleName()+"</td><td valign=top>");
		boolean handled=false;
		if (o instanceof Header[]) {
			ret.append("<table>");
			handled=true;
			for (final Header h: ((Header[]) o)) {
				ret.append("<tr><td valign=top>").append(h.getName()).append("</td><td valign=top>").append(h.getValue()).append("</td></tr>");
			}
			ret.append("</table>");
		}
		if (BasicHttpRequest.class.isAssignableFrom(o.getClass())) {
			handled=true;
			ret.append("<table border=1>");
			final BasicHttpRequest req=(BasicHttpRequest) o;
			ret.append("<tr><td valign=top colspan=4>").append(req.getRequestLine()).append("</td></td>");
			for (final Header header:req.getAllHeaders()) {
				ret.append("<tr><td valign=top>").append(toHTML(header.getName())).append("</td>");
				ret.append("<td valign=top>").append(toHTML(header.getValue())).append("</td></tr>");
			}
			ret.append("</table>");
		}
		if (JSONObject.class.isAssignableFrom(o.getClass())) {
			handled=true;
			ret.append("<table border=1>");
			final JSONObject json=(JSONObject) o;
			final Map<String,Object> map=json.toMap();
			for (final Map.Entry<String,Object> entry: map.entrySet()) {
				ret.append("<tr><td valign=top>").append(toHTML(entry.getKey())).append("</td>");
				ret.append("<td valign=top>").append(toHTML(entry.getValue())).append("</td></tr>");
			}
			ret.append("</table>");
		}		
		if (Map.class.isAssignableFrom(o.getClass())) {
			handled=true;
			ret.append("<table border=1>");
			@SuppressWarnings("unchecked")
			final Map<Object,Object> map=(Map<Object,Object>) o;
			for (final Map.Entry<Object,Object> entry: map.entrySet()) {
				ret.append("<tr><td valign=top>").append(toHTML(entry.getKey())).append("</td>");
				ret.append("<td valign=top>").append(toHTML(entry.getValue())).append("</td></tr>");
			}
			ret.append("</table>");
		}
		if (!handled) { ret.append(o); }
		return ret.toString();
	}

}

