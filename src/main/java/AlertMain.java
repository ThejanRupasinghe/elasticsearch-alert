import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by thejan on 7/13/17.
 */
public class AlertMain {

    public static final int TIMEPERIOD = 25000;

    private static int previousExecutionTime = 0;

    public static void main(String[] args) throws InterruptedException{

        boolean goodConfiguration = Configuration.INSTANCE.readConfigurationFile();

        if (goodConfiguration) {

            QueryElasticsearch.INSTANCE.configure();
            MailSender.INSTANCE.configureMail();
        }

        Timer time = new Timer();
        ScheduledTask st = new ScheduledTask();
        time.schedule(st, 0, TIMEPERIOD);

    }

    public static int getPreviousExecutionTime() {
        return previousExecutionTime;
    }

    public static void setPreviousExecutionTime(int previousExecutionTime) {
        AlertMain.previousExecutionTime = previousExecutionTime;
    }

}
