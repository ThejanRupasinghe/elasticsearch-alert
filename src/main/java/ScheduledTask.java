import java.util.Date;
import java.util.TimerTask;

/**
 * Created by thejan on 7/14/17.
 */
public class ScheduledTask extends TimerTask {

    public void run() {

        Date startDate = new Date();
        String response = QueryElasticsearch.INSTANCE.query();
        if (!(response == null)) {
            System.out.println(response);
                    MailSender.INSTANCE.sendMail(response);

        }
        Date endDate = new Date();
        long diff = endDate.getTime()-startDate.getTime();
        System.out.println(diff);
        AlertMain.setPreviousExecutionTime((int)diff);
//        System.out.println(diff/1000);
//        if ( ) {
//
//        }
    }
}
