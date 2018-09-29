package net.coagulate.Core;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Iain Price
 */
public class MailTools {
    public static void mail(String server,String fromname,String fromaddress,String toname,String toaddress,String subject, String body) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", server);
        props.put("mail.from", fromname + " <" + fromaddress + ">");
        Session session = Session.getInstance(props, null);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom();
        msg.setRecipients(Message.RecipientType.TO, "toname <"+toaddress+">");
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setContent(body, "text/html");
        Transport.send(msg);
    }
}
