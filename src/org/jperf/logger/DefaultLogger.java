package org.jperf.logger;

/**
 * @author Andy Grove
 */
public class DefaultLogger implements JPerfLogger {

    public void error(String message, Throwable th) {
        System.out.println("[ERROR] " + message);
        th.printStackTrace();
    }

    public void warn(String message) {
        System.out.println("[WARN] " + message);
    }

    public void info(String message) {
        System.out.println("[INFO] " + message);
    }

    public void debug(String message) {
        System.out.println("[DEBUG] " + message);
    }
}
