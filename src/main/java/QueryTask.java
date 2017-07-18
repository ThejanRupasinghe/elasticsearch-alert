/**
 * Created by thejan on 7/14/17.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;

/**
 * This class extends TimerTask and runs in a defined interval to query
 */
public class QueryTask extends TimerTask {

    private static final Logger logger = LogManager.getLogger(QueryTask.class);

    /**
     * Implements run in TimerTask
     */
    public void run() {

        // Start time of task execution
        Date startDate = new Date();

        // Querying and taking responses
        ArrayList<String> response = QueryElasticsearch.INSTANCE.query();

        if ( response == null ) {   // If response equals null means no elasticsearch node found

            AlertMain.getQueryTask().cancel();

        } else {

            // Empty response is handled my sendMail method
            boolean sendSuccess = MailSender.INSTANCE.sendMail(response);

            if (!( sendSuccess )) { // If error in email sending terminate repeating the query

                AlertMain.getQueryTask().cancel();

            } else {

                // End time of the task execution
                Date endDate = new Date();

                // Takes time taken to execute
                long timeTakenToExecute = endDate.getTime()-startDate.getTime();

                logger.info("Time for the execution : " + timeTakenToExecute + " ms");

                // Sets previous execution time
                AlertMain.setPreviousExecutionTime((int)timeTakenToExecute);

            }

        }

    }

}
