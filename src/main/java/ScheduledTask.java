import java.util.Date;
import java.util.TimerTask;

/**
 * Created by thejan on 7/14/17.
 */
public class ScheduledTask extends TimerTask {

    public void run() {

        String response = QueryElasticsearch.INSTANCE.query();

//        MailSender.INSTANCE.sendMail(response);
    }
}
