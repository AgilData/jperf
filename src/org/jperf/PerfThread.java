package org.jperf;

import org.jperf.util.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread that repeatedly invokes the PerfTest in a tight loop and updates the shared counter after each 
 * invocation.
 */
public class PerfThread extends Thread {

    /**
     * Logger.
     */
    protected static final Logger logger = LoggerFactory.getLogger(PerfThread.class);

    /**
     * The test to invoke.
     */
    protected PerfTest test;

    /**
     * Reference to shared counter.
     */
    protected AtomicLong counter;
    
    protected boolean stopOnError;

    /**
     * Flag that indicates if a request has been made to terminate this test thread.
     */
    protected volatile boolean stopRequested;

    protected PerfTestRunner runner;

    protected long startTime;

    /**
     * Number of iterations on this thread.
     */
    protected volatile long iterations;

    public PerfThread(PerfTestRunner runner, PerfTest test, AtomicLong counter, boolean stopOnError) {
        this.runner = runner;
        this.test = test;
        this.counter = counter;
        this.stopOnError = stopOnError;
    }

    public void run() {
        try {
            startTime = System.currentTimeMillis();

//            long t = System.currentTimeMillis();
            while (!stopRequested() && !runner.stop) {
                try {
                    test.test();

//                    long now = System.currentTimeMillis();
//                    if (now-t > 10) {
                        counter.incrementAndGet();
//                    }

                    iterations++;
                }
                catch (Throwable th) {
                    logger.error( "Test iteration threw an exception", th );
                    if (stopOnError) {
                        throw th;
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
        logger.debug("Interrupting thread {}", this.getName());
        this.interrupt();
    }

    public synchronized boolean stopRequested() {
        return stopRequested;
    }

    public float getAverageIterationTime() {
        return (System.currentTimeMillis()-startTime)/(1.0f*iterations);
    }
}
