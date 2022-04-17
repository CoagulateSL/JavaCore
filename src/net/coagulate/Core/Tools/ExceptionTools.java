package net.coagulate.Core.Tools;

import net.coagulate.Core.Exceptions.SystemException;
import net.coagulate.Core.Exceptions.UserException;

import javax.annotation.Nonnull;

/**
 * @author Iain Price
 */
public abstract class ExceptionTools {
	// ---------- STATICS ----------
	@Nonnull
	public static String dumpException(@Nonnull final Throwable e) {
		final StringBuilder p=new StringBuilder();
		if (e.getCause()!=null) {
			p.append(dumpException(e.getCause()));
		}
		p.append("<h3>").append(e.getClass().getName()).append(" - ").append(e.getLocalizedMessage()).append("</h3>");
		for (final StackTraceElement st: e.getStackTrace()) {
			p.append("<pre>");
			if (!st.getClassName().startsWith("net.coagulate.")) { p.append("<del>"); }
			p.append(" at ")
			 .append(st.getClassName())
			 .append(".")
			 .append(st.getMethodName())
			 .append("(")
			 .append(st.getFileName())
			 .append(":")
			 .append(st.getLineNumber())
			 .append(")");
			if (!st.getClassName().startsWith("net.coagulate.")) { p.append("</del>"); }
			p.append("</pre>");
		}
		return p.toString();
	}

	@Nonnull
	public static String toString(@Nonnull final Throwable e) {
		final StringBuilder p=new StringBuilder();
		if (e.getCause()!=null) {
			p.append(toString(e.getCause()));
		}
		p.append("***EXCEPTION***: ").append(e.getClass().getName()).append(" - ").append(e.getLocalizedMessage()).append("\n");
		Throwable loop=e;
		while (loop!=null) {
			p.append("Exception: ").append(loop.getClass().getSimpleName()).append(" - ").append(loop.getLocalizedMessage()).append("\n");
			for (final StackTraceElement st: loop.getStackTrace()) {
				p.append(" at ").append(st.getClassName()).append(".").append(st.getMethodName()).
					append("(").append(st.getFileName()).append(":").append(st.getLineNumber()).append(")").append("\n");
			}
			loop=loop.getCause();
		}
		return p.toString();
	}

	@Nonnull
	public static String toHTML(@Nonnull final Throwable e) { return dumpException(e); }

    public static String getPertinent(final Throwable t) {
		if (t == null) {
			return "Null exception";
		}
		final StackTraceElement[] frames = t.getStackTrace();
		if (frames.length == 0) {
			return "[NoStackTrace] - " + t.getLocalizedMessage();
		}
		StackTraceElement frame = frames[0];
		boolean islocal = false;
		for (final StackTraceElement stackTraceElement : frames) {
			if (!islocal) {
				if (stackTraceElement.getClassName().startsWith("net.coagulate.")) {
					frame = stackTraceElement;
					islocal = true;
				}
			}
		}
		String prefix="UNHANDLED";
		if (UserException.class.isAssignableFrom(t.getClass())) { prefix="User"; }
		if (SystemException.class.isAssignableFrom(t.getClass())) { prefix="ERROR"; }
		return prefix+"["+
				frame.getClassName().replaceFirst("net.coagulate.","")+
				"."+frame.getMethodName()+":"+frame.getLineNumber()+"] - "+t.getClass().getSimpleName()+" - "+t.getLocalizedMessage();
    }
}
