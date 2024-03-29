package net.coagulate.Core.Tools;

import net.coagulate.Core.Exceptions.SystemException;
import net.coagulate.Core.Exceptions.UserException;

import javax.annotation.Nonnull;
import javax.mail.MessagingException;
import java.util.*;
import java.util.logging.*;

import static java.util.logging.Level.*;

/**
 * @author Iain Price
 */
public class LogHandler extends Handler {
	
	private static final   Set<Throwable>      alreadymailed   =new HashSet<>();
			//may eventually overflow, if we spam exceptions :P
	private static final   Map<String,Integer> suppressioncount=new HashMap<>();
	private static final   Map<String,Date>    suppressionclear=new HashMap<>();
	@Nonnull public static String              mailprefix      ="[UNKNOWN]";
	
	public LogHandler() {
		setLevel(ALL);
	}
	
	// ---------- STATICS ----------
	public static void initialise() {
		LogManager.getLogManager().reset();
		Logger.getLogger("").setLevel(ALL);
		Logger.getLogger("").addHandler(new LogHandler());
	}
	
	public static void alreadyMailed(final Throwable t) {
		alreadymailed.add(t);
	}
	
	// ---------- INSTANCE ----------
	@Override
	public void publish(@Nonnull final LogRecord record) {
		try {
			final Object[] parameters=record.getParameters();
			final Level level=record.getLevel();
			String system=record.getLoggerName();
			String classname=record.getSourceClassName();
			final String method=record.getSourceMethodName();
			final long thread=record.getLongThreadID();
			String message=record.getMessage();
			final long when=record.getMillis();
			if (!system.contains(".")) {
				System.out.println("***** UNDOTTED LOGGER SYSTEM DETECTED: "+system);
			}
			if (!system.startsWith("net.coagulate")&&(level==CONFIG||level==FINE||level==FINER||level==FINEST)) {
				// not our systems, or anything we care about most of the time.  thanks.
				return;
			}
			system=system.replaceFirst("net\\.coagulate\\.","");
			while (classname.contains(".")) {
				classname=classname.substring(classname.indexOf('.')+1);
			}
			if (parameters!=null&&parameters.length>0) {
				for (int i=0;i<parameters.length;i++) {
					final String replacewith=parameters[i].toString();
					//if (replacewith.length()>100) {
					//    System.out.println("WARNING: Large substitution");
					//} else {
					message=message.replaceAll("\\{"+i+"}",parameters[i].toString());
					//}
				}
			}
			System.out.println(
					formatLevel(level)+"#"+postpad(String.valueOf(thread),4)+" "+system+" - "+message+" (@"+classname+
					"."+method+")");
			
			if ((level==null||level.intValue()>FINE.intValue())&&record.getThrown()!=null) {
				final Throwable thrown=record.getThrown();
				// Stop here after the console print but before the mail print if this is a suppressed exception
				if (UserException.class.isAssignableFrom(thrown.getClass())) {
					if (((UserException)thrown).suppressed()) {
						return;
					}
				}
				if (SystemException.class.isAssignableFrom(thrown.getClass())) {
					if (((SystemException)thrown).suppressed()) {
						return;
					}
				}
				
				//if (!thrown instanceof UserException) {
				try {
					if (alreadymailed.contains(thrown)) {
						alreadymailed.remove(thrown);
					} else {
						if (suppress(thrown)) {
							System.out.println("Exception Log Suppressed "+getCount(thrown)+"x"+getSignature(thrown));
						} else {
							System.out.println(ExceptionTools.toString(thrown));
							MailTools.mail(mailprefix+" {NoLog} "+thrown.getClass().getSimpleName()+" - "+message+" - "+
							               thrown.getLocalizedMessage(),ExceptionTools.toHTML(thrown));
						}
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
		} catch (final Throwable t) {
			System.out.println("PANIC!!!");
			System.out.println("PANIC!!!");
			System.out.println("PANIC!!!");
			System.out.println("Exception in logger:"+t);
			t.printStackTrace();
			
		}
	}
	
	@Nonnull
	private String formatLevel(final Level level) {
		if (level==CONFIG) {
			return "conf";
		}
		if (level==FINE) {
			return "D   ";
		}
		if (level==FINER) {
			return "d   ";
		}
		if (level==FINEST) {
			return "_ ";
		}
		if (level==INFO) {
			return "Info";
		}
		if (level==SEVERE) {
			return "XXXX";
		}
		if (level==WARNING) {
			return "WARN";
		}
		return "????";
	}
	
	// ----- Internal Instance -----
	@Nonnull
	String postpad(@Nonnull final String in,final int len) {
		final StringBuilder pad=new StringBuilder(in);
		while (pad.length()<len) {
			pad.append(" ");
		}
		return pad.toString();
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
	
	@Nonnull
	public static String getSignature(final Throwable t) {
		final StackTraceElement[] stacktrace=t.getStackTrace();
		if (stacktrace.length==0) {
			return "NO-STACK-TRACE??";
		}
		try {
			final StackTraceElement ste=stacktrace[0];
			return t.getClass().getSimpleName()+"@"+ste.getClassName()+"."+ste.getMethodName()+":"+ste.getFileName()+
			       ":"+ste.getLineNumber();
		} catch (final RuntimeException runtimeexception) {
			System.out.println("Exception during GETSIGNATURE");
			runtimeexception.printStackTrace();
		}
		return "";
	}
	
	// ----- Internal Statics -----
	static synchronized void considerExpiring(final String signature) {
		if (suppressionclear.containsKey(signature)) {
			if (suppressionclear.get(signature).before(new Date())) {
				suppressionclear.remove(signature);
				suppressioncount.remove(signature);
			}
		}
	}
	
	@Override
	public void flush() {
	}
	
	@Override
	public void close() {
	}
}
