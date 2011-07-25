package org.jperf.example;

import org.jperf.PerfTest;
import org.jperf.PerfTestFactory;
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

    private static void doTest(final Class theClass) throws Exception {
        PerfTestRunner r = new PerfTestRunner();
        r.setMinThread(1);
        r.setMaxThread(10);
        r.setTestPeriod(500);
        r.run(new PerfTestFactory() {
            @Override
            public PerfTest createPerfTest() throws Exception {
                return (PerfTest) theClass.newInstance();
            }
        });
    }
}
