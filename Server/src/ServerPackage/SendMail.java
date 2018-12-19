package email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	String username;
	String password;
	Session session;
	public SendMail(String username, String password) {
		this.username = username;
		this.password = password;
		this.session = userAuthorization(username, password);
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public Session getSession() {
		return session;
	}

	public static void main(String[] args) {
		SendMail mail = new SendMail("srgb2015@mymail.pomona.edu", "Tennis15");
		mail.sendEmail("srgb2015@mymail.pomona.edu", "Room47 Test", "This is a test");
	}
	
	public Session userAuthorization(String username, String password) {
		Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.office365.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
          new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
          });
        return session;
	}
	
	public void sendEmail(String to, String subject, String body) {
		try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
	}
}
