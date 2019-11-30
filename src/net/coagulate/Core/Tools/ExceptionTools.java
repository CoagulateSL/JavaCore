package net.coagulate.Core.Tools;

/**
 * @author Iain Price
 */
public abstract class ExceptionTools {
	public static String dumpException(Throwable e) {
		StringBuilder p = new StringBuilder();
		if (e.getCause() != null) { p.append(dumpException(e.getCause())); }
		p.append("<h3>").append(e.getClass().getName()).append(" - ").append(e.getLocalizedMessage()).append("</h3>");
		for (StackTraceElement st : e.getStackTrace()) {
			p.append("<pre>");
			p.append(" at ").append(st.getClassName()).append(".").append(st.getMethodName()).append("(").append(st.getFileName()).append(":").append(st.getLineNumber()).append(")");
			p.append("</pre>");
		}
		return p.toString();
	}

	public static String toString(Throwable e) {
		StringBuilder p = new StringBuilder();
		if (e.getCause() != null) { p.append(dumpException(e.getCause())); }
		p.append("***EXCEPTION***: ").append(e.getClass().getName()).append(" - ").append(e.getLocalizedMessage()).append("\n");
		for (StackTraceElement st : e.getStackTrace()) {
			p.append("___exception___: ").append(st.getClassName()).append(".").append(st.getMethodName()).append(":").append(st.getLineNumber()).append("\n");
		}
		return p.toString();
	}

	public static String toHTML(Throwable e) { return dumpException(e); }

}
