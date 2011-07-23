package org.jperf.example;

import org.jperf.PerfTestRunner;

/**
 * Runs the examples.
 *
 */
public class Main {

    public static void main(String[] args) throws Exception {
        doTest(NewDateFormatTest.class);
        doTest(SynchronizedDateFormatTest.class);
        doTest(ThreadLocalDateFormatTest.class);
    }

    private static void doTest(Class theClass) throws Exception {
        PerfTestRunner r = new PerfTestRunner();
        r.setMinClient(1);
        r.setMaxClient(10);
        r.setTestPeriod(500);
        r.run(theClass);
    }
}
