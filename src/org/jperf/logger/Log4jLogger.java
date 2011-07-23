package org.jperf.logger;

import org.apache.log4j.Logger;
import org.jperf.logger.JPerfLogger;

/**
 * Log4j delegate.
 * 
 * @author Andy Grove
 */
public class Log4jLogger implements JPerfLogger {

    private static final Logger logger = Logger.getLogger( "org.jperf" );

    public void error(String message, Throwable th) {
        logger.error( message, th );
    }

    public void warn(String message) {
        logger.warn( message );
    }

    public void info(String message) {
        if (logger.isInfoEnabled()) {
            logger.info( message );
        }
    }

    public void debug(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug( message );
        }
    }
}
