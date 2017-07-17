import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by thejan on 7/13/17.
 */
public enum MailSender {

    INSTANCE;

    private Properties properties = null;
    private String to_email ;
    private String from_email ;

    /**
     *
     * @return
     */
    public boolean configureMail () {

        properties = new Properties();
        properties.put("mail.smtp.starttls.enable", true);
        properties.put("mail.smtp.host", Configuration.INSTANCE.getEmailHost());
        properties.put("mail.smtp.user", Configuration.INSTANCE.getEmailUsername());
        properties.put("mail.smtp.password", Configuration.INSTANCE.getEmailPassword());
        properties.put("mail.smtp.port", Configuration.INSTANCE.getEmailPort());
        properties.put("mail.smtp.auth", true);

        to_email = Configuration.INSTANCE.getEmailReceiversAddress();
        from_email = Configuration.INSTANCE.getEmailUsername();

        return true;

    }

    /**
     *
     * @param messageList
     * @return
     */
    public boolean sendMail(ArrayList<String> messageList) {

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {    // Define the authenticator
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Configuration.INSTANCE.getEmailUsername(),Configuration.INSTANCE.getEmailPassword());
                    }
                });

        try {
            // Create a default MimeMessage object.
            MimeMessage mimeMessage = new MimeMessage(session);

            // Set From: header field of the header.
            mimeMessage.setFrom(new InternetAddress(from_email));

            // Set To: header field of the header.
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to_email));

            // Set Subject: header field
            mimeMessage.setSubject("[ERROR LOG]");

            if (messageList.size()>0){
                String message = "";
                for (int i=0; i<messageList.size(); i++){
                    message += (i+1) + ") " + messageList.get(i) + "\n";
                }

                System.out.println(message);
//            System.exit(0);
                // Now set the actual message
                mimeMessage.setText(message);

                // Send message
                Transport.send(mimeMessage);
                System.out.println("Sent message successfully....");
            } else {
                System.out.println("no email sending");
            }

            return true;
        }catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Error in email sending");
            return false;
        }

    }
}
