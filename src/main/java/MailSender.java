/**
 * Created by thejan on 7/13/17.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Properties;

/**
 * This configures email and sends emails. Singleton class.
 */
public enum MailSender {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(MailSender.class);

    private Properties properties = null;
    private String to_email ;
    private String from_email ;


    /**
     * This method configures the email sender
     *
     * @return true in successful configuration
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
     * This method sends mail to configured email when results are found in the query
     *
     * @param messageList of found messages in response of the query
     * @return true on successful mail send, false in error
     */
    public boolean sendMail(ArrayList<String> messageList) {

        if ( ! ( messageList.isEmpty() ) ) {    // Authenticate and configure only if messages are found

            // Gets the default Session object.
            Session session = Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator() {    // Define the authenticator
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(Configuration.INSTANCE.getEmailUsername(), Configuration.INSTANCE.getEmailPassword());
                        }
                    });


            try {

                // Creates a default MimeMessage object.
                MimeMessage mimeMessage = new MimeMessage(session);

                // Sets From: header field of the header.
                mimeMessage.setFrom(new InternetAddress(from_email));

                // Sets To: header field of the header.
                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to_email));

                // Sets Subject: header field
                mimeMessage.setSubject("[ERROR LOG]");


                String message = "";
                for (int i = 0; i < messageList.size(); i++) {
                    message += (i + 1) + ") " + messageList.get(i) + "\n";
                }


                // Sets the body of the email
                mimeMessage.setText(message);

                // Sends the email
                Transport.send(mimeMessage);

                // If no errors in sending
                logger.info("Email sent successfully");

                return true;

            }catch(MessagingException e){

//                e.printStackTrace();
                logger.error("Error in email sending. Check your email configurations.");
                return false;

            }

        } else {

            // Empty responses, no email sending , nothing to log.
            return true;
        }

    }
}
