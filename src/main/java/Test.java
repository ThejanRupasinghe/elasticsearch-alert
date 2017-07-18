import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.elasticsearch.index.query.QueryBuilders.*;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;
import javax.swing.*;

import java.io.FileReader;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by thejan on 7/12/17.
 */
public class Test {

//    private final static Logger LOGGER = LogManager.getLogger(Test.class);

    public static void main(String[] args) {
//
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
////        dateFormat.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        String formattedDate = dateFormat.format(date);
        System.out.println(formattedDate);
//        Calendar cal =Calendar.getInstance();
//        cal.setTime(date);
//        cal.add(Calendar.SECOND, 20);
//        System.out.println(cal.getTime());
//        System.out.println(formattedDate);

//        System.out.println(1834/1000);
    }

    public void testMethod () {
        JSONParser parser = new JSONParser();

        /** Taking Date in the format logstash-2017.07.12 **/
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date date = new Date();
        String formattedDate = dateFormat.format(date);
        System.out.println(formattedDate);

        Settings settings = Settings.builder().put("cluster.name","elasticsearch").build();

        TransportClient client = new PreBuiltTransportClient(settings);
        try {
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"),9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        QueryBuilder qb = matchQuery("message", "[ERROR]");
        SearchResponse response = client.prepareSearch("logstash-2017.07.12")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qb)                // Query
                .setFrom(0).setSize(1).setExplain(false)
                .get();

        System.out.println(response.toString());
        System.out.println(response.getHits().getHits().length);

        String log_message = response.getHits().getHits()[0].getSource().get("message").toString();
//        for (SearchHit hit : response.getHits().getHits()) {
        System.out.println(log_message);
//        }

        Map<String, Object> template_params = new HashMap<String, Object>();
        template_params.put("param_gender", "male");

//        SearchResponse sr = new SearchTemplateRequestBuilder(client)
//                .setScript("template_gender")
//                .setScriptType(ScriptType.FILE)
//                .setScriptParams(template_params)
//                .setRequest(new SearchRequest())
//                .get()
//                .getResponse();

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", true); // added this line
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.user", "thejanrupasinghe@gmail.com");
        props.put("mail.smtp.password", "etrlhcihevrdfmhm");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", true);

        // Recipient's email ID needs to be mentioned.
        String to = "thejan@wso2.com";

        // Sender's email ID needs to be mentioned
        String from = "thejanrupasinghe@gmail.com";

        // Get the default Session object.
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {    // Define the authenticator
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("thejanrupasinghe@gmail.com","etrlhcihevrdfmhm");
                    }
                });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("[ERROR LOG]");

            // Now set the actual message
//            message.setText(log_message);
            message.setText("message");

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }

//        client.close();

    }
}
