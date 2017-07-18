import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by thejan on 7/17/17.
 */

public enum  AppLogger {

    INSTANCE;

    private Logger logger;

    AppLogger(){

        logger = LogManager.getLogger(AppLogger.class);
    }

    public Logger getLogger () {
        return logger;
    }
}
