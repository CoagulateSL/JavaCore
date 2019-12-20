package net.coagulate.Core.Tools;

import net.coagulate.Core.Exceptions.System.SystemInitialisationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * @author Iain Price
 */
public abstract class MailTools {
	@Nullable
	public static String defaultserver;
	@Nullable
	public static String defaultfromname;
	@Nullable
	public static String defaultfromaddress;
	@Nullable
	public static String defaulttoname;
	@Nullable
	public static String defaulttoaddress;

	public static void mail(final String server,
	                        final String fromname,
	                        final String fromaddress,
	                        final String toname,
	                        final String toaddress,
	                        final String subject,
	                        final String body) throws MessagingException
	{
		final Properties props=new Properties();
		props.put("mail.smtp.host",server);
		props.put("mail.from",fromname+" <"+fromaddress+">");
		final Session session=Session.getInstance(props,null);
		final MimeMessage msg=new MimeMessage(session);
		msg.setFrom();
		msg.setRecipients(Message.RecipientType.TO,"toname <"+toaddress+">");
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setContent(body,"text/html");
		Transport.send(msg);
	}

	public static void mail(final String fromname,
	                        final String fromaddress,
	                        final String toname,
	                        final String toaddress,
	                        final String subject,
	                        final String body) throws MessagingException
	{
		if (defaultserver==null) {
			throw new SystemInitialisationException("Mail called without default server configured");
		}
		mail(defaultserver,fromname,fromaddress,toname,toaddress,subject,body);
	}

	public static void mail(final String toname,
	                        final String toaddress,
	                        final String subject,
	                        final String body) throws MessagingException
	{
		if (defaultfromname==null || defaultfromaddress==null) {
			throw new SystemInitialisationException("Mail called without default from address configured");
		}
		mail(defaultfromname,defaultfromaddress,toname,toaddress,subject,body);
	}

	public static void mail(final String subject,
	                        final String body) throws MessagingException
	{
		if (defaulttoname==null || defaulttoaddress==null) {
			throw new SystemInitialisationException("Mail called without default to address configured");
		}
		mail(defaulttoname,defaulttoaddress,subject,body);
	}

	public static void logTrace(final String subject,
	                            final String intro)
	{
		final StringBuilder body=new StringBuilder(intro+"\n<br>\n");
		for (final StackTraceElement ele: Thread.currentThread().getStackTrace()) {
			body.append("Caller: ")
			    .append(ele.getClassName())
			    .append("/")
			    .append(ele.getMethodName())
			    .append(":")
			    .append(ele.getLineNumber())
			    .append("\n<br>\n");
		}
		try {
			MailTools.mail("Trace: "+subject,body.toString());
		} catch (@Nonnull final MessagingException ee) {
		}
	}

}
