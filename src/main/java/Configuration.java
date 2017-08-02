/**
 * Created by thejan on 7/13/17.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class does the configurations of MailSender and matching patterns. Singleton class
 */
public enum Configuration {

    INSTANCE;

    private static final Logger logger = LogManager.getLogger(Configuration.class);

    private static final String FILENAME = "config.json";

    // Configuration variables taking from the config.json file
    private String emailHost;
    private String emailPort;
    private String emailUsername;
    private String emailPassword;
    private ArrayList<String> emailToAddresses;
    private ArrayList<String> emailCCAddresses;

    private String elasticsearchHost;
    private String elasticsearchClusterName;

    private ArrayList<String> matchList;


    /**
     *  This reads the config.json file in the same folder and changes the configuration variables
     *
     * @return true in successful configuration , false in error
     */
    public boolean readConfigurationFile(){

        JSONParser parser = new JSONParser();

        Object parserObject;

        // Reads the config.json file and parse it to Parser Object which will be later converted to Json Object.
        try {

            parserObject = parser.parse(new FileReader(FILENAME));

        } catch (IOException e) {

//            e.printStackTrace();
            logger.error("Configuration file not found");
            return false;

        } catch (ParseException e) {

//            e.printStackTrace();
            logger.error("Error in configuration file");
            return false;

        }

        // Converts Parser Object to Json Object and changes the variables
        JSONObject jsonObject = (JSONObject) parserObject;

        // polling time will be taken from the configuration file
        long pollingTime;

        try {

            JSONObject emailDetailsJson = (JSONObject) jsonObject.get("email_details");
            emailHost = (String) emailDetailsJson.get("email_host");
            emailPort = (emailDetailsJson.get("email_port")).toString();
            emailUsername = (String) emailDetailsJson.get("email_username");
            emailPassword = (String) emailDetailsJson.get("email_password");

            // Creating TO and CC email lists
            JSONArray toEmailListJson = (JSONArray) emailDetailsJson.get("email_to_addresses");

            emailToAddresses = new ArrayList<String>();

            for (Object matchJson : toEmailListJson) {
                emailToAddresses.add(matchJson.toString());
            }

            JSONArray ccEmailListJson = (JSONArray) emailDetailsJson.get("email_cc_addresses");

            emailCCAddresses = new ArrayList<String>();

            for (Object matchJson : ccEmailListJson) {
                emailCCAddresses.add(matchJson.toString());
            }


            JSONObject elasticsearchDetailsJson = (JSONObject) jsonObject.get("elasticsearch_details");
            elasticsearchHost = (String) elasticsearchDetailsJson.get("host");
            elasticsearchClusterName = (String) elasticsearchDetailsJson.get("cluster_name");

            JSONArray matchListJson = (JSONArray) jsonObject.get("match_list");

            matchList = new ArrayList<String>();

            for (Object matchJson : matchListJson) {
                matchList.add(matchJson.toString());
            }

            pollingTime = (Long) jsonObject.get("polling_time");

            // Sets query time period in AlertMain
            AlertMain.setTimePeriod((int) pollingTime);

        }catch (ClassCastException e) { // Handles any casting exception

            logger.error("Error in Configuration details");
            return false;

        }

        // If any configuration variable is still null, terminates the method and returns false
        if( emailHost == null || emailPort == null || emailUsername == null || emailPassword == null || matchList == null || emailToAddresses == null || emailToAddresses.isEmpty()) {

            logger.error("Error in Configuration details");
            return false;

        } else {

            logger.info("Getting configuration successful..");
            logger.info("TLS encryption enabled");
            logger.info("Email Host : " + emailHost);
            logger.info("Email Port : " + emailPort);
            logger.info("Email Username : " + emailUsername);
//            logger.info("Email Password: "+emailPassword);
            logger.info("Email Receivers' Addresses :  TO : " + emailToAddresses + "  CC : " + emailCCAddresses);
            logger.info("Elasticsearch Host : " + elasticsearchHost);
            logger.info("Elasticsearch Cluster Name : " + elasticsearchClusterName);
            logger.info("Match List : " + matchList.toString());
            logger.info("Querying Time Interval : " + pollingTime + " ms");

            return true;    // Return true on successful configuration
        }

    }


    /* Getters for private configuration variables */

    /**
     *
      * @return Email Host  eg: smtp.gmail.com
     */
    public String getEmailHost() {
        return emailHost;
    }

    /**
     *
     * @return Email Port of the Email Host  eg: 587 , 465
     */
    public String getEmailPort() {
        return emailPort;
    }

    /**
     *
     * @return Email Username ( Email address of the sender )
     */
    public String getEmailUsername() {
        return emailUsername;
    }

    /**
     *
     * @return Email Password ( Email address password of the sender / Gmail generated app key )
     */
    public String getEmailPassword() {
        return emailPassword;
    }

    /**
     *
     * @return String Array List of strings to put in the match query
     */
    public ArrayList<String> getMatchList() {
        return matchList;
    }

    /**
     *
     * @return Email Address of the email alert receivers in "cc" field
     */
    public ArrayList<String> getEmailCCAddresses() {
        return emailCCAddresses;
    }

    /**
     *
     * @return Email Address of the email alert receivers in "to" field
     */
    public ArrayList<String> getEmailToAddresses() {
        return emailToAddresses;
    }

    /**
     *
     * @return Elasticsearch Host  eg: localhost / IP address
     */
    public String getElasticsearchHost() {
        return elasticsearchHost;
    }

    /**
     *
     * @return Elasticsearch Cluster Name ( Where to query )
     */
    public String getElasticsearchClusterName() {
        return elasticsearchClusterName;
    }


}
