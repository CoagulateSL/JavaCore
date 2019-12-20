package net.coagulate.Core.Tools;

import javax.annotation.Nonnull;
import javax.mail.MessagingException;
import java.util.*;
import java.util.logging.*;

/**
 * @author Iain Price
 */
public class LogHandler extends Handler {

	private static final Set<Throwable> alreadymailed=new HashSet<>(); //may eventually overflow, if we spam exceptions :P
	private static final Map<String,Integer> suppressioncount=new HashMap<>();
	private static final Map<String,Date> suppressionclear=new HashMap<>();
	@Nonnull
	public static String mailprefix="[UNKNOWN]";

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

	@Nonnull
	public static String getSignature(final Throwable t) {
		try {
			final StackTraceElement ste=t.getStackTrace()[0];
			return t.getClass().getSimpleName()+"@"+ste.getClassName()+"."+ste.getMethodName()+":"+ste.getFileName()+":"+ste.getLineNumber();
		}
		catch (final RuntimeException ignored) {
			System.out.println("Exception during GETSIGNATURE");
			ignored.printStackTrace();
		}
		return "";
	}

	static synchronized void considerExpiring(final String signature) {
		if (suppressionclear.containsKey(signature)) {
			if (suppressionclear.get(signature).before(new Date())) {
				suppressionclear.remove(signature);
				suppressioncount.remove(signature);
			}
		}
	}

	public static boolean suppress(final Throwable t) {
		final String signature=getSignature(t);
		if (signature.isEmpty()) {
			return false;
		}
		considerExpiring(signature);
		if (suppressioncount.containsKey(signature)) {
			suppressioncount.put(signature,suppressioncount.get(signature)+1);
			return true;
		}
		final Calendar expires=Calendar.getInstance();
		expires.add(Calendar.MINUTE,15);
		suppressionclear.put(signature,expires.getTime());
		suppressioncount.put(signature,1);
		return false;
	}

	public static int getCount(final Throwable t) {
		final String signature=getSignature(t);
		if (signature.isEmpty()) {
			return -1;
		}
		if (suppressioncount.containsKey(signature)) {
			return suppressioncount.get(signature);
		}
		return -1;
	}

	@Override
	public void publish(@Nonnull final LogRecord record) {
		final Object[] parameters=record.getParameters();
		final Level level=record.getLevel();
		String system=record.getLoggerName();
		String classname=record.getSourceClassName();
		final String method=record.getSourceMethodName();
		final int thread=record.getThreadID();
		String message=record.getMessage();
		final long when=record.getMillis();
		if (!system.startsWith("net.coagulate.")) {
			return;
		}
		system=system.replaceFirst("net\\.coagulate\\.","");
		while (classname.contains(".")) {
			classname=classname.substring(classname.indexOf(".")+1);
		}
		if (parameters!=null && parameters.length>0) {
			for (int i=0;i<parameters.length;i++) {
				final String replacewith=parameters[i].toString();
				//if (replacewith.length()>100) {
				//    System.out.println("WARNING: Large substitution");
				//} else {
				message=message.replaceAll("\\{"+i+"}",parameters[i].toString());
				//}
			}
		}
		System.out.println(formatLevel(level)+"#"+postpad(thread+"",4)+" "+system+" - "+message+" (@"+classname+"."+method+")");
		if ((level==null || level.intValue()>Level.FINE.intValue()) && record.getThrown()!=null) {
			final Throwable thrown=record.getThrown();
			//if (!thrown instanceof UserException) {
			if (suppress(thrown)) {
				System.out.println("Exception Log Suppressed "+getCount(thrown)+"x"+getSignature(thrown));
			}
			else {
				System.out.println(ExceptionTools.toString(thrown));
				try {
					if (alreadymailed.contains(thrown)) {
						alreadymailed.remove(thrown);
					}
					else {
						MailTools.mail(mailprefix+" {NoLog} "+thrown.getClass().getSimpleName()+" - "+message+" - "+thrown.getLocalizedMessage(),
						               ExceptionTools.toHTML(thrown)
						              );
					}
				}
				catch (@Nonnull final MessagingException ex) {
					System.out.println("EXCEPTION IN EXCEPTION MAILER");
					System.out.println("EXCEPTION IN EXCEPTION MAILER");
					System.out.println("EXCEPTION IN EXCEPTION MAILER");
					System.out.println("EXCEPTION IN EXCEPTION MAILER");
					System.out.println("EXCEPTION IN EXCEPTION MAILER");
					System.out.println(ExceptionTools.toString(ex));
				}
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
		if (level==Level.CONFIG) {
			return "conf";
		}
		if (level==Level.FINE) {
			return "D   ";
		}
		if (level==Level.FINER) {
			return "d   ";
		}
		if (level==Level.FINEST) {
			return "_ ";
		}
		if (level==Level.INFO) {
			return "Info";
		}
		if (level==Level.SEVERE) {
			return "XXXX";
		}
		if (level==Level.WARNING) {
			return "WARN";
		}
		return "????";
	}

	@Nonnull
	String postpad(@Nonnull final String in,
	               final int len) {
		final StringBuilder pad=new StringBuilder(in);
		while (pad.length()<len) {
			pad.append(" ");
		}
		return pad.toString();
	}
}
