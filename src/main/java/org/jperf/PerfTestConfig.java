package org.jperf;

/** Test configuration. Should be created via builder obtained from JPerf.newConfigBuilder(). */
public class PerfTestConfig {

  protected int minThreads = 1;
  protected int maxThreads = 10;
  protected int threadIncrement = 1;
  protected int duration = 1000;
  protected ResultWriter resultWriter = new ResultWriterStdout();
  protected PerfTestFactory testFactory;

  @Override
  public String toString() {
    return "PerfTestConfig{" +
        "minThreads=" + minThreads +
        ", maxThreads=" + maxThreads +
        ", threadIncrement=" + threadIncrement +
        ", duration=" + duration +
        '}';
  }
}
