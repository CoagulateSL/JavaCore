package net.coagulate.Core.Tools;

import org.json.JSONObject;

import java.io.StringWriter;

public class JsonTools {
	// ---------- STATICS ----------
	public static String jsonToString(final JSONObject json) {
		final StringWriter sw=new StringWriter();
		json.write(sw,4,0);
		return sw+"\n";
	}
	// ----- Internal Statics -----
}
