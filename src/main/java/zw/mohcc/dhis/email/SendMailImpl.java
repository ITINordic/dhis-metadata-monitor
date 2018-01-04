package zw.mohcc.dhis.email;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Charles Chigoriwa
 */
public class SendMailImpl {

    private static final String SMTP_HOST_NAME = "localhost";
    private static final String SMTP_PORT = "25";
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    private String username;
    private String password;

    public SendMailImpl() {
    }

    public SendMailImpl(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /* (non-Javadoc)
   * @see com.commerce4j.storefront.utils.SendMail#sendMessage(java.lang.String, java.lang.String[], java.lang.String, java.lang.String)
     */
    public void sendMessage(
            String from, String recipients[],
            String subject, String message
    ) throws MessagingException {

        // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        boolean debug = true;

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "false");
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.fallback", "true");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        session.setDebug(debug);
        Message msg = new MimeMessage(session);
        InternetAddress addressFrom = new InternetAddress(from);
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/html");
        Transport.send(msg, addressTo);
    }

    /**
     * JavaBean Getter, Gets the username current value.
     *
     * @return The username current value.
     */
    public String getUsername() {
        return username;
    }

    /**
     * JavaBean Setter, Sets value to username.
     *
     * @param username The value of username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * JavaBean Getter, Gets the password current value.
     *
     * @return The password current value.
     */
    public String getPassword() {
        return password;
    }

    /**
     * JavaBean Setter, Sets value to password.
     *
     * @param password The value of password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public static void main(String[] args) throws MessagingException {
        SendMailImpl sendMailImpl = new SendMailImpl();
        sendMailImpl.sendMessage("info@sadombo.itinordic.com", new String[]{"cchigoriwa@gmail.com"}, "Test", "Test Message");

    }

}
