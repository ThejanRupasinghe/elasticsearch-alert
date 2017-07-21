/**
 * Created by thejan on 7/13/17.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * This configures email and sends emails. Singleton class.
 */
public enum MailSender {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(MailSender.class);

    private Properties properties = null;
    private ArrayList<String> to_email_list ;
    private ArrayList<String> cc_email_list;

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

        to_email_list = Configuration.INSTANCE.getEmailToAddresses();
        cc_email_list = Configuration.INSTANCE.getEmailCCAddresses();
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

                // Sets TO: header field of the header.
                Address[] toAddresses = new Address[to_email_list.size()];
                for( int i=0; i<to_email_list.size();i++){
                    toAddresses[i] = new InternetAddress(to_email_list.get(i));
                }

                mimeMessage.addRecipients(Message.RecipientType.TO, toAddresses );


                // Only if there are CC addresses
                if (!( cc_email_list.isEmpty() )) {
                    // Sets CC: header field of the header.
                    Address[] ccAddresses = new Address[cc_email_list.size()];
                    for( int i=0; i<cc_email_list.size();i++){
                        ccAddresses[i] = new InternetAddress(cc_email_list.get(i));
                    }

                    mimeMessage.addRecipients(Message.RecipientType.CC, ccAddresses );
                }


                // Sets Subject: header field
                mimeMessage.setSubject("[ERROR LOG]");


                String message = "";
                for (int i = 0; i < messageList.size(); i++) {
                    message += (i + 1) + ") " + messageList.get(i) + "\n\n";
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
