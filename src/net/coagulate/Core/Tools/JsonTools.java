package net.coagulate.Core.Tools;

import org.json.JSONObject;

import java.io.StringWriter;

public class JsonTools {
	// ----- Internal Statics -----
	public static String jsonToString(JSONObject json) {
		StringWriter sw=new StringWriter();
		json.write(sw,4,0);
		return sw.toString()+"\n";
	}
}
