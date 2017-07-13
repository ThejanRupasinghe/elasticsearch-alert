/**
 * Created by thejan on 7/13/17.
 */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class will do the configurations of MailSender and matching patterns
 */
public class Configuration {

    private final String FILENAME = "config.json";

    private static String emailHost;
    private static String emailPort;
    private static String emailUsername;

    private static String emailPassword;

    private ArrayList<String> matchList;

    public boolean readConfigurationFile(){

        JSONParser parser = new JSONParser();

        Object parserObject = null;
        try {
             parserObject = parser.parse(new FileReader(FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in configuration file");
        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("Error in configuration file");
        }

        JSONObject jsonObject = (JSONObject) parserObject;

        JSONObject emailDetailsJson = (JSONObject) jsonObject.get("email_details");
        emailHost = (String) emailDetailsJson.get("email_host");
        emailPort = (emailDetailsJson.get("email_port")).toString();
        emailUsername = (String) emailDetailsJson.get("email_username");
        emailPassword = (String) emailDetailsJson.get("email_password");

        JSONArray matchListJson = (JSONArray) jsonObject.get("match_list");

        matchList = new ArrayList<String>();

        for (Object matchJson : matchListJson) {
            System.out.println(matchJson.toString());
            matchList.add(matchJson.toString());
        }

        if( emailHost==null || emailPort==null || emailUsername==null || emailPassword==null || matchList==null) {
            System.out.println("ERROR");
            return false;
        } else {

            System.out.println("Getting configuration successful..");
            System.out.println("TLS encryption enabled");
            System.out.println("Email Host : "+emailHost);
            System.out.println("Email Port : "+emailPort);
            System.out.println("Email Username : "+emailUsername);
            System.out.println("Match List : "+matchList.toString());
//            System.out.println("Email Password: "+emailPassword);

            return true;
        }

    }

    public static String getEmailHost() {
        return emailHost;
    }

    public static String getEmailPort() {
        return emailPort;
    }

    public static String getEmailUsername() {
        return emailUsername;
    }

    public static String getEmailPassword() {
        return emailPassword;
    }

    public ArrayList<String> getMatchList() {
        return matchList;
    }

}
