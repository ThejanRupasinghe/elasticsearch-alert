import java.util.Date;
import java.util.TimerTask;

/**
 * Created by thejan on 7/14/17.
 */
public class ScheduledTask extends TimerTask {

    public void run() {

        String response = QueryElasticsearch.INSTANCE.query();
        if (!(response==null)){
            System.out.println(response);
//        MailSender.INSTANCE.sendMail(response);

        }
    }
}
