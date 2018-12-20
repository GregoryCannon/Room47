package ServerPackage;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailManager {
	String username;
	String password;
	Session session;

    private static final String DEFAULT_USERNAME = "jcga2015@mymail.pomona.edu";
    private static final String DEFAULT_PASSWORD = "Mapo5172";

	public EmailManager(String username, String password) {
		this.username = username;
		this.password = password;
		this.session = userAuthorization(username, password);
	}

    public EmailManager(){
        this(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

	public static void main(String[] args) {
		EmailManager mail = new EmailManager("jcga2015@mymail.pomona.edu", "Mapo5172");
		mail.sendEmail("jcga2015@mymail.pomona.edu", "Room47 Test", "This is a test");
	}
	
	private Session userAuthorization(String username, String password) {
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
