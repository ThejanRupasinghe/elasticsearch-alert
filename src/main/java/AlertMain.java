import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by thejan on 7/13/17.
 */
public class AlertMain {

    public static final int TIMEPERIOD = 25000;

    private static int previousExecutionTime = 0;

    private static QueryTask queryTask;

    public static void main(String[] args) throws InterruptedException{

        boolean goodConfiguration = Configuration.INSTANCE.readConfigurationFile();

        if (goodConfiguration) {

            QueryElasticsearch.INSTANCE.configure();
            MailSender.INSTANCE.configureMail();
        }

        Timer timer = new Timer();
        queryTask = new QueryTask();
        timer.schedule(queryTask, 0, TIMEPERIOD);
        timer.cancel();

    }

    public static QueryTask getQueryTask () {
        return queryTask;
    }
    public static int getPreviousExecutionTime() {
        return previousExecutionTime;
    }

    public static void setPreviousExecutionTime(int previousExecutionTime) {
        AlertMain.previousExecutionTime = previousExecutionTime;
    }

}
