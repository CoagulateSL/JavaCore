package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;

/**
 * @author Iain Price
 */
public abstract class ExceptionTools {
	@Nonnull
	public static String dumpException(@Nonnull final Throwable e) {
		final StringBuilder p=new StringBuilder();
		if (e.getCause()!=null) { p.append(dumpException(e.getCause())); }
		p.append("<h3>").append(e.getClass().getName()).append(" - ").append(e.getLocalizedMessage()).append("</h3>");
		for (final StackTraceElement st: e.getStackTrace()) {
			p.append("<pre>");
			p.append(" at ")
			 .append(st.getClassName())
			 .append(".")
			 .append(st.getMethodName())
			 .append("(")
			 .append(st.getFileName())
			 .append(":")
			 .append(st.getLineNumber())
			 .append(")");
			p.append("</pre>");
		}
		return p.toString();
	}

	@Nonnull
	public static String toString(@Nonnull final Throwable e) {
		final StringBuilder p=new StringBuilder();
		if (e.getCause()!=null) { p.append(dumpException(e.getCause())); }
		p.append("***EXCEPTION***: ")
		 .append(e.getClass().getName())
		 .append(" - ")
		 .append(e.getLocalizedMessage())
		 .append("\n");
		Throwable loop=e;
		while (loop!=null) {
			p.append("___exception___ - ").append(loop.getLocalizedMessage());
			for (final StackTraceElement st: loop.getStackTrace()) {
				p.append("___exception___: ")
				 .append(st.getClassName())
				 .append(".")
				 .append(st.getMethodName())
				 .append(":")
				 .append(st.getLineNumber())
				 .append("\n");
			}
			loop=loop.getCause();
		}
		return p.toString();
	}

	@Nonnull
	public static String toHTML(@Nonnull final Throwable e) { return dumpException(e); }

}
