package org.jperf;

import org.jperf.util.Counter;
import org.jperf.logger.JPerfLogger;

/**
 * Created by IntelliJ IDEA.
* User: andy
* Date: Oct 25, 2008
* Time: 9:40:19 PM
* To change this template use File | Settings | File Templates.
*/
public class PerfThread extends Thread {

    protected PerfTest test;
    protected Counter counter;
    protected JPerfLogger logger;

    protected float averageIterationTime = 0;

    protected boolean stopOnError;

    /**
     * Flag that indicates if a request has been made to terminate this test thread.
     */
    protected volatile boolean stopRequested;

    protected PerfTestRunner runner;

    public PerfThread(PerfTestRunner runner, PerfTest test, Counter counter, JPerfLogger logger, boolean stopOnError) {
        this.runner = runner;
        this.test = test;
        this.counter = counter;
        this.logger = logger;
        this.stopOnError = stopOnError;
    }

    public void run() {
        try {
            long t1 = System.currentTimeMillis();
            int iterations = 0;
            while (!stopRequested() && !runner.stop) {
                try {
                    test.test();
                    counter.next();
                    // update average tx time
                    long t2 = System.currentTimeMillis();
                    iterations++;
                    averageIterationTime = (t2-t1)/(1.0f*iterations);
                }
                catch (Throwable th) {
                    if (stopOnError) {
                        throw th;
                    }
                    else {
                        logger.error( "Test iteration threw an exception", th );
                    }
                }
            }
        } catch (Throwable e) {
            logger.error( "Test iteration threw an exception and stopOnError=true", e );
        }
        finally {
            try {
                test.tearDown();
            } catch (Throwable e) {
                logger.error( "Test tearDown threw an exception", e );
            }
        }
    }

    public synchronized void requestStop() {
        stopRequested = true;
        this.interrupt();
    }

    public synchronized boolean stopRequested() {
        return stopRequested;
    }

    public JPerfLogger getLogger() {
        return logger;
    }

    public void setLogger(JPerfLogger logger) {
        this.logger = logger;
    }

    public float getAverageIterationTime() {
        return averageIterationTime;
    }
}
