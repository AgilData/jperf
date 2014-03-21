package org.jperf;

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

    protected PerfTestRunner runner;

    protected volatile long startTime;

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

            long t = System.currentTimeMillis();
            long localCounter = 0;

            while (true) {
                try {
                    test.test();
                    localCounter++;

                    // update shared memory only every 10 ms to reduce thread contention
                    final long now = System.currentTimeMillis();
                    if (now-t > 10) {
                        counter.addAndGet(localCounter);
                        iterations += localCounter;
                        localCounter = 0;
                        t = now;
                    }

                }
                catch (InterruptedException ie) {
                    break;
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

    public float getAverageIterationTime() {
        return (System.currentTimeMillis()-startTime)/(1.0f*iterations);
    }
}
