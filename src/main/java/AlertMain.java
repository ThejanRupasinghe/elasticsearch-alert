import java.util.Timer;

/**
 * Created by thejan on 7/13/17.
 *
 * Contains main method which configures the application and initiates the query task timer
 */
public class AlertMain {

    private static int timePeriod = 25000;

    private static int previousExecutionTime = 0;

    private static Timer timer;

    public static void main(String[] args) {

        boolean elasticConfigure = false;
        boolean mailConfigure = false;

        // Reads the config.json file and load the configurations
        boolean goodConfiguration = Configuration.INSTANCE.readConfigurationFile();

        if ( goodConfiguration ) {    // If loading configuration is successful

            // Configures Elasticsearch and email sending clients
            elasticConfigure = QueryElasticsearch.INSTANCE.configure();
            mailConfigure = MailSender.INSTANCE.configureMail();

            // Starts timer only if Elastic and mail clients are configured well
            if ( elasticConfigure && mailConfigure ) {

                timer = new Timer();
                QueryTask queryTask = new QueryTask();
                timer.schedule(queryTask, 0, timePeriod);

            }

        }

    }

    public static Timer getTimer () {
        return timer;
    }

    public static int getTimePeriod() {
        return timePeriod;
    }

    public static void setTimePeriod(int timePeriod) {
        AlertMain.timePeriod = timePeriod;
    }

    public static int getPreviousExecutionTime() {
        return previousExecutionTime;
    }

    public static void setPreviousExecutionTime(int previousExecutionTime) {
        AlertMain.previousExecutionTime = previousExecutionTime;
    }

}
