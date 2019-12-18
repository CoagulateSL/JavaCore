package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;
import javax.mail.MessagingException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.*;

/**
 * @author Iain Price
 */
public class LogHandler extends Handler {

	@Nonnull
	public static String mailprefix = "[UNKNOWN]";
	private static final Set<Throwable> alreadymailed = new HashSet<>(); //may eventually overflow, if we spam exceptions :P

	public LogHandler() {
		super();
		setLevel(Level.ALL);
	}

	public static void initialise() {
		LogManager.getLogManager().reset();
		Logger.getLogger("").setLevel(Level.ALL);
		Logger.getLogger("").addHandler(new LogHandler());
	}

	public static void alreadyMailed(final Throwable t) { alreadymailed.add(t); }

	@Override
	public void publish(@Nonnull final LogRecord record) {
		final Object[] parameters = record.getParameters();
		final Level level = record.getLevel();
		String system = record.getLoggerName();
		String classname = record.getSourceClassName();
		final String method = record.getSourceMethodName();
		final int thread = record.getThreadID();
		String message = record.getMessage();
		final long when = record.getMillis();
		if (!system.startsWith("net.coagulate.")) { return; }
		system = system.replaceFirst("net\\.coagulate\\.", "");
		while (classname.contains(".")) {
			classname = classname.substring(classname.indexOf(".") + 1);
		}
		if (parameters != null && parameters.length > 0) {
			for (int i = 0; i < parameters.length; i++) {
				final String replacewith = parameters[i].toString();
				//if (replacewith.length()>100) {
				//    System.out.println("WARNING: Large substitution");
				//} else {
				message = message.replaceAll("\\{" + i + "}", parameters[i].toString());
				//}
			}
		}
		System.out.println(formatLevel(level) + "#" + postpad(thread + "", 4) + " " + system + " - " + message + " (@" + classname + "." + method + ")");
		if ((level == null || level.intValue() > Level.FINE.intValue()) && record.getThrown() != null) {
			final Throwable thrown = record.getThrown();
			//if (!thrown instanceof UserException) {
			System.out.println(ExceptionTools.toString(thrown));
			try {
				if (alreadymailed.contains(thrown)) { alreadymailed.remove(thrown); } else {
					MailTools.mail(mailprefix + " {NoLog} " + thrown.getClass().getSimpleName()+" - "+message + " - " + thrown.getLocalizedMessage(), ExceptionTools.toHTML(thrown));
				}
			} catch (@Nonnull final MessagingException ex) {
				System.out.println("EXCEPTION IN EXCEPTION MAILER");
				System.out.println("EXCEPTION IN EXCEPTION MAILER");
				System.out.println("EXCEPTION IN EXCEPTION MAILER");
				System.out.println("EXCEPTION IN EXCEPTION MAILER");
				System.out.println("EXCEPTION IN EXCEPTION MAILER");
				System.out.println(ExceptionTools.toString(ex));
			}
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
	}

	@Nonnull
	private String formatLevel(final Level level) {
		if (level == Level.CONFIG) { return "conf"; }
		if (level == Level.FINE) { return "D   "; }
		if (level == Level.FINER) { return "d   "; }
		if (level == Level.FINEST) { return "_ "; }
		if (level == Level.INFO) { return "Info"; }
		if (level == Level.SEVERE) { return "XXXX"; }
		if (level == Level.WARNING) { return "WARN"; }
		return "????";
	}

	@Nonnull
	String postpad(@Nonnull final String in, final int len) {
		final StringBuilder pad = new StringBuilder(in);
		while (pad.length() < len) { pad.append(" "); }
		return pad.toString();
	}
}
