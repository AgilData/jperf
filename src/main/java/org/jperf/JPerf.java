package org.jperf;

import org.apache.commons.cli.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JPerf {

  protected static final String CMD_LINE_ARG_CLASS = "class";
  protected static final String CMD_LINE_ARG_MIN_THREAD = "min";
  protected static final String CMD_LINE_ARG_MAX_THREAD = "max";
  protected static final String CMD_LINE_ARG_THREAD_INCREMENT = "increment";
  protected static final String CMD_LINE_ARG_DURATION = "duration";

  /** Command-line entry point. */
  public static void main(String[] args) throws Exception {
    System.exit(doMain(args));
  }

  /** Command-line entry point for unit tests. */
  public static int doMain(String[] args) throws Exception {

    // parse command-line args
    CommandLine cmd = new BasicParser().parse(getCommandLineOptions(), args);
    if (!cmd.hasOption(CMD_LINE_ARG_CLASS)) {
      showUsage();
      return -1;
    }

    final Class testClass = Class.forName(cmd.getOptionValue(CMD_LINE_ARG_CLASS));

    // build the configuration
    final ConfigBuilder builder = newConfigBuilder();
    if (cmd.hasOption(CMD_LINE_ARG_MIN_THREAD)) {
      builder.minThreads(Integer.parseInt(cmd.getOptionValue(CMD_LINE_ARG_MIN_THREAD)));
    }
    if (cmd.hasOption(CMD_LINE_ARG_MAX_THREAD)) {
      builder.maxThreads(Integer.parseInt(cmd.getOptionValue(CMD_LINE_ARG_MAX_THREAD)));
    }
    if (cmd.hasOption(CMD_LINE_ARG_THREAD_INCREMENT)) {
      builder.threadIncrement(Integer.parseInt(cmd.getOptionValue(CMD_LINE_ARG_THREAD_INCREMENT)));
    }
    if (cmd.hasOption(CMD_LINE_ARG_DURATION)) {
      builder.duration(Integer.parseInt(cmd.getOptionValue(CMD_LINE_ARG_DURATION)));
    }
    builder.testFactory(() -> (PerfTest) testClass.newInstance());

    // execute the test
    run(builder.build());

    return 0;
  }

  private static Options getCommandLineOptions() {
    Options options = new Options();
    options.addOption(new Option(CMD_LINE_ARG_CLASS, true, "Name of class that implemented org.jperf.PerfTest"));
    options.addOption(new Option(CMD_LINE_ARG_MIN_THREAD, true, "The number of threads to start testing with"));
    options.addOption(new Option(CMD_LINE_ARG_MAX_THREAD, true, "The maximum number of threads to test with"));
    options.addOption(new Option(CMD_LINE_ARG_THREAD_INCREMENT, true, "The number of threads to increment by"));
    options.addOption(new Option(CMD_LINE_ARG_DURATION, true, "The duration in milliseconds (per thread level)"));
    return options;
  }

  private static void showUsage() {
    new HelpFormatter().printHelp("jperf", getCommandLineOptions(), true);
    System.out.println();
    System.out.println("All arguments are optional, except for 'class'.");
  }

  public static void run(final PerfTestConfig config) throws Exception {

    // create executors and test runners (one of each per thread)
    final ExecutorService exec[] = new ExecutorService[config.maxThreads];
    final SingleTestRunner testRunner[] = new SingleTestRunner[config.maxThreads];

    try {

      config.resultWriter.writeHeader(config);

      // start testing
      for (int threadCount = config.minThreads; threadCount <= config.maxThreads; threadCount += config.threadIncrement) {

        for (int i = 0; i < threadCount; i++) {
          if (exec[i] == null) {
            // create the executor
            exec[i] = Executors.newSingleThreadExecutor();
            // creat the test runner
            testRunner[i] = new SingleTestRunner(config.testFactory.create());
            // start the test runner
            exec[i].execute(testRunner[i]);
          }
        }

        // reset the counters
        for (int i = 0; i < threadCount; i++) {
          testRunner[i].getCounter().set(0);
        }

        final long start = System.currentTimeMillis();
        Thread.sleep(config.duration);
        final long actualDuration = System.currentTimeMillis() - start;

        // collect the totals
        long samples = 0;
        for (int i = 0; i < threadCount; i++) {
          samples += testRunner[i].getCounter().get();
        }

        config.resultWriter.writeResult(threadCount, actualDuration, samples);
      }

    } finally {

      config.resultWriter.close();

      // stop threads
      for (SingleTestRunner r : testRunner) {
        if (r != null) {
          r.stop();
        }
      }
      for (ExecutorService e : exec) {
        if (e != null) {
          e.shutdown();
        }
      }
      for (ExecutorService e : exec) {
        if (e != null) {
          e.awaitTermination(5, TimeUnit.SECONDS);
        }
      }
    }
  }

  public static ConfigBuilder newConfigBuilder() {
    return new DefaultConfigBuilder();
  }

}
