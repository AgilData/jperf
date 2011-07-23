package org.jperf.logger;

/**
 * Generic Logger interface so that JPerf can easily integrate with Ant, Log4j, etc.
 * 
 * @author Andy Grove
 */
public interface JPerfLogger {
    public void error(String message, Throwable th);
    public void warn(String message);
    public void info(String message);
    public void debug(String message);
}
