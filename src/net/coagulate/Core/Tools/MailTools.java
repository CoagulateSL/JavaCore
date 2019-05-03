package net.coagulate.Core.Tools;

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
	public static String defaultserver = null;
	public static String defaultfromname = null;
	public static String defaultfromaddress = null;
	public static String defaulttoname = null;
	public static String defaulttoaddress = null;

	public static void mail(String server, String fromname, String fromaddress, String toname, String toaddress, String subject, String body) throws MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", server);
		props.put("mail.from", fromname + " <" + fromaddress + ">");
		Session session = Session.getInstance(props, null);
		MimeMessage msg = new MimeMessage(session);
		msg.setFrom();
		msg.setRecipients(Message.RecipientType.TO, "toname <" + toaddress + ">");
		msg.setSubject(subject);
		msg.setSentDate(new Date());
		msg.setContent(body, "text/html");
		Transport.send(msg);
	}

	public static void mail(String fromname, String fromaddress, String toname, String toaddress, String subject, String body) throws MessagingException {
		if (defaultserver == null) { throw new SystemException("Mail called without default server configured"); }
		mail(defaultserver, fromname, fromaddress, toname, toaddress, subject, body);
	}

	public static void mail(String toname, String toaddress, String subject, String body) throws MessagingException {
		if (defaultfromname == null || defaultfromaddress == null) {
			throw new SystemException("Mail called without default from address configured");
		}
		mail(defaultfromname, defaultfromaddress, toname, toaddress, subject, body);
	}

	public static void mail(String subject, String body) throws MessagingException {
		if (defaulttoname == null || defaulttoaddress == null) {
			throw new SystemException("Mail called without default to address configured");
		}
		mail(defaulttoname, defaulttoaddress, subject, body);
	}

}
