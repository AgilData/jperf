package org.jperf;

import org.jperf.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The JPerf test runner launches one or more threads to run instances of PerfTest and
 * measures performance and scalability.
 *
 * @author Andy Grove
 */
public class PerfTestRunner {

    /**
     * Logger.
     */
    protected static final Logger logger = LoggerFactory.getLogger(PerfTestRunner.class);

    /**
     * Counter for measuring throughput.
     */
    protected final AtomicLong counter = new AtomicLong();

    /**
     * Minimum number of threads to run.
     */
    protected int minThread = 1;

    /**
     * Maximum number of threads to run.
     */
    protected int maxThread = 10;

    /**
     * Number of threads to increase clients by for each test period.
     */
    protected int threadIncrement = 1;

    /**
     * Duration to run tests for at each thread count.
     */
    protected int testPeriod = 500;

    /**
     * Optional file to output results to.
     */
    protected String resultFilename;

    /**
     * Specify if test thread should terminate after an exception.
     */
    protected boolean stopThreadOnError = true;

    /**
     * Temporary storage for throughput results.
     */
    protected List<PerfResult> results;

    protected List<PerfThread> clientThreads = new ArrayList<PerfThread>();

    public volatile boolean stop;

    long testCreateTime = -1;

    long testSetupTime = -1;

    public PerfTestRunner() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                stop();
            }
        }));
    }

    public List<PerfResult> run(final PerfTestFactory testFactory) throws IOException {

        logger.info("JPerf is testing " + testFactory.getClass());

        DecimalFormat fmt = new DecimalFormat("#,##0");

        results = new ArrayList<PerfResult>();

        for (int threadCount = minThread; threadCount <= maxThread && !stop; threadCount += threadIncrement) {

            while (clientThreads.size() < threadCount) {
                PerfTest test;
                try {
                    test = createTestInstance(testFactory);
                } catch (Throwable e) {
                    logger.error("Failed to create test", e);
                    break;
                }

                logger.debug("Starting thread to run tests");

                PerfThread perfThread = new PerfThread(this, test, counter, stopThreadOnError);
                clientThreads.add(perfThread);
                perfThread.start();
            }

            final long t1 = System.currentTimeMillis();

            // reset the counter again to ensure we don't measure iterations
            // that already happened before we started the timer
            counter.set(0);

            logger.debug("Sleeping for {} ms", testPeriod);

            /*
            long sleepStart = System.currentTimeMillis();
            while (!stop && (System.currentTimeMillis() - sleepStart) < testPeriod) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
            */
            try {
                Thread.sleep(testPeriod);
            } catch (InterruptedException e) {
                logger.warn("Tests interrupted", e);
                break;
            }

            // get the iteration count just before we stop the timer
            final long iterations = counter.getAndSet(0);
            final long t2 = System.currentTimeMillis();

            // calculations
            final long testPeriodMeasured = t2 - t1;
            final float tps = iterations * 1000.0f / testPeriodMeasured;

            // show a warning if the test period was more than 5% out
            final float testPeriodVariance = (testPeriodMeasured - testPeriod) / (1.0f * testPeriod);
            if (testPeriodVariance < -0.05 || testPeriodVariance > 0.05) {
                logger.warn("Test period configured as {} but was actually {}", testPeriod, testPeriodMeasured);
            }

            Runtime rt = Runtime.getRuntime();

            final PerfResult result = new PerfResult(threadCount, iterations, testPeriodMeasured, tps,
                    rt.freeMemory(), rt.maxMemory(), rt.totalMemory(), Thread.activeCount());

            result.setTestCreate(testCreateTime);
            result.setTestSetup(testSetupTime);
            results.add(result);

            String strThreadCount = fmt.format(threadCount);
            while (strThreadCount.length() < 4) {
                strThreadCount = " " + strThreadCount;
            }

            // calculate average transaction time
            float avgIterationTime = 0;
            for (PerfThread thread : clientThreads) {
                avgIterationTime += thread.getAverageIterationTime();
            }
            avgIterationTime /= (1.0f * clientThreads.size());

            logger.info(
                    "{} test threads: {} calls/sec; avg call: {} ms; {} KB mem used; {} active threads.",
                    strThreadCount,
                    fmt.format(tps),
                    fmt.format(avgIterationTime),
                    fmt.format((result.getMemTotal() - result.getMemFree()) / 1024.0f),
                    result.getActiveThreadCount()
            );
        }

        stop();

        if (resultFilename == null) {
            resultFilename = "JPerf-" + testFactory.getClass().getName();
        }

        File resultFile = new File(resultFilename + ".xml");
        logger.info("Writing results to " + resultFile.getAbsolutePath());
        FileOutputStream os = new FileOutputStream(resultFile);
        writeXmlResults(os, testFactory.getClass().getName());
        os.close();

        resultFile = new File(resultFilename + ".csv");
        logger.info("Writing results to " + resultFile.getAbsolutePath());
        os = new FileOutputStream(resultFile);
        writeCsvResults(os);
        os.close();

        return results;

    }

    public void stop() {

        logger.info("Stopping tests");

        stop = true;

        // ask threads to finish
        logger.info("Instructing threads to stop");
        for (PerfThread clientThread : clientThreads) {
            clientThread.interrupt();
        }

        // wait for all threads to stop (with a 5 second max wait right now - should be configurable really)
        int aliveCount = maxThread;
        long startWait = System.currentTimeMillis();
        while (aliveCount > 0) {
            aliveCount = 0;

            if ((System.currentTimeMillis() - startWait) > 5000) {
                logger.info("Timed out waiting for threads to stop");
                break;
            }

            for (PerfThread clientThread : clientThreads) {
                if (clientThread.isAlive()) {
                    aliveCount++;
                }
            }
            if (aliveCount > 0) {
                logger.info("Waiting for {} threads to stop", aliveCount);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                }
            }
        }

    }

    private PerfTest createTestInstance(PerfTestFactory factory) throws Exception {

        Timer timer = new Timer();
        timer.start();
        PerfTest test = factory.createPerfTest();
        timer.stop();
        testCreateTime = timer.value();

        // setup the test
        logger.debug("Calling setUp");

        timer.reset();
        timer.start();
        test.setUp();
        timer.stop();
        testSetupTime = timer.value();

        logger.debug("Called setUp OK");

        // run the test once to make sure the test is working
        logger.debug("Verifying test");
        test.test();
        logger.debug("Verified test OK");
        return test;
    }

    private PerfTest createTest(Class testClass, Object[] param) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        PerfTest test = null;
        if (param == null || param.length == 0) {
            Constructor noArgCtor = testClass.getConstructor();
            if (noArgCtor == null) {
                throw new IllegalArgumentException(
                        "Test class '" +
                                testClass.getName() +
                                "' does not have a no-arg public constructor " +
                                "and no arguments were supplied"
                );
            }
            test = (PerfTest) noArgCtor.newInstance();
        } else {
            for (Constructor c : testClass.getConstructors()) {
                final Class paramTypes[] = c.getParameterTypes();
                if (paramTypes.length == param.length) {
                    boolean isSuitable = true;
                    for (int i = 0; i < paramTypes.length; i++) {
                        if (!paramTypes[i].isAssignableFrom(param[i].getClass())) {
                            logger.debug(paramTypes[i].getClass() + " is not assignable from " + param[i].getClass());
                            isSuitable = false;
                            break;
                        }
                    }
                    if (isSuitable) {
                        test = (PerfTest) c.newInstance(param);
                        break;
                    }
                }
            }
            if (test == null) {
                throw new IllegalArgumentException(
                        "Test class '" +
                                testClass.getName() +
                                "' does not have a suitable public constructor " +
                                "for the supplied arguments"
                );
            }
        }
        return test;
    }

    private void writeXmlResults(OutputStream os, String testClassName) throws IOException {
        PrintWriter w = new PrintWriter(os);

        DateFormat df = DateFormat.getDateTimeInstance();

        final InetAddress host = InetAddress.getLocalHost();

        StringBuffer root = new StringBuffer();
        root.append("<jperf ");
        root.append("testClass=\"").append(testClassName).append("\" ");
        root.append("timestamp=\"").append(df.format(new Date())).append("\" ");
        root.append("hostName=\"").append(host.getHostName()).append("\" ");
        root.append("hostAddress=\"").append(host.getHostAddress()).append("\" ");
        root.append(">");
        w.println(root);

        w.println("<environment>");
        List keys = new ArrayList();
        final Properties properties = System.getProperties();
        keys.addAll(properties.keySet());
        Collections.sort(keys);
        for (Object key : keys) {
            w.println("<property key=\"" + sanitize(key) + "\" value=\"" + sanitize(properties.get(key)) + "\" />");
        }
        w.println("</environment>");
        w.println("<results>");
        for (int i = 0; i < results.size(); i++) {
            PerfResult r = results.get(i);
            StringBuffer buf = new StringBuffer();
            buf.append("<results threadCount=\"").append(r.getTestThreadCount());
            buf.append("\" iterations=\"").append(r.getIterations());
            buf.append("\" time=\"").append(r.getTime());
            buf.append("\" throughput=\"").append(r.getThroughput());
            buf.append("\" testCreateTime=\"").append(r.getTestCreate());
            buf.append("\" testSetupTime=\"").append(r.getTestSetup());
            buf.append("\" maxMemory=\"").append(r.getMemMax());
            buf.append("\" totalMemory=\"").append(r.getMemTotal());
            buf.append("\" freeMemory=\"").append(r.getMemFree());
            buf.append("\" usedMemory=\"").append(r.getMemTotal() - r.getMemFree());
            buf.append("\" />");
            w.println(buf.toString());
        }
        w.println("</results>");
        w.println("</jperf>");
        w.close();
    }

    private void writeCsvResults(OutputStream os) throws IOException {
        PrintWriter w = new PrintWriter(os);

        StringBuffer buf = new StringBuffer();
        buf.append("threadCount");
        buf.append(", iterations");
        buf.append(", time");
        buf.append(", throughput");
        buf.append(", testCreateTime");
        buf.append(", testSetupTime");
        buf.append(", maxMemory");
        buf.append(", totalMemory");
        buf.append(", freeMemory");
        buf.append(", usedMemory");
        w.println(buf.toString());
        for (int i = 0; i < results.size(); i++) {
            PerfResult r = results.get(i);
            buf.setLength(0);
            buf.append(r.getTestThreadCount());
            buf.append(",").append(r.getIterations());
            buf.append(",").append(r.getTime());
            buf.append(",").append(r.getThroughput());
            buf.append(",").append(r.getTestCreate());
            buf.append(",").append(r.getTestSetup());
            buf.append(",").append(r.getMemMax());
            buf.append(",").append(r.getMemTotal());
            buf.append(",").append(r.getMemFree());
            buf.append(",").append(r.getMemTotal() - r.getMemFree());
            w.println(buf.toString());
        }
        w.close();
    }

    private String sanitize(Object _str) {
        if (_str == null) {
            return "";
        }
        String str;
        if (_str instanceof String) {
            str = (String) _str;
        } else {
            str = _str.toString();
        }
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isLetter(ch)
                    || Character.isDigit(ch)
                    || Character.isWhitespace(ch)
                    || "-=_+!$%�^&*(),.;:'@#~/?[]{}`\\|".indexOf(ch) >= 0) {
                ret.append(ch);
            } else {
                switch (ch) {
                    case '\'':
                        ret.append("&apos;");
                        break;
                    case '\"':
                        ret.append("&quot;");
                        break;
                    case '<':
                        ret.append("&lt;");
                        break;
                    case '>':
                        ret.append("&gt;");
                        break;
                    default:
                        ret.append('?');
                        break;
                }
            }
        }
        return ret.toString();
    }

    public int getTestPeriod() {
        return testPeriod;
    }

    public void setTestPeriod(int testPeriod) {
        this.testPeriod = testPeriod;
    }

    public int getMaxThread() {
        return maxThread;
    }

    public void setMaxThread(int maxThread) {
        this.maxThread = maxThread;
    }

    public int getMinThread() {
        return minThread;
    }

    public void setMinThread(int minThread) {
        this.minThread = minThread;
    }

    public int getThreadIncrement() {
        return threadIncrement;
    }

    public void setThreadIncrement(int threadIncrement) {
        this.threadIncrement = threadIncrement;
    }

    public boolean isStopThreadOnError() {
        return stopThreadOnError;
    }

    public void setStopThreadOnError(boolean stopThreadOnError) {
        this.stopThreadOnError = stopThreadOnError;
    }

    public String getResultFilename() {
        return resultFilename;
    }

    public void setResultFilename(String resultFilename) {
        this.resultFilename = resultFilename;
    }

}
