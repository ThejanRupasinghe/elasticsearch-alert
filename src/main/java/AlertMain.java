import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by thejan on 7/13/17.
 */
public class AlertMain {

    public static final int TIMEPERIOD = 20000;

    public static void main(String[] args) throws InterruptedException{

        boolean goodConfiguration = Configuration.INSTANCE.readConfigurationFile();

        if (goodConfiguration) {

            QueryElasticsearch.INSTANCE.configure();
            MailSender.INSTANCE.configureMail();
        }

//        QueryElasticsearch.INSTANCE.query();

        Timer time = new Timer();
        ScheduledTask st = new ScheduledTask();
        time.schedule(st, 0, TIMEPERIOD);



    }
}
