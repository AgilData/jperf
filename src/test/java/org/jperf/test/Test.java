package org.jperf.test;

import org.jperf.PerfTestConfig;
import org.jperf.JPerf;
import org.jperf.PerfTest;

public class Test {

  @org.junit.Test
  public void test() throws Exception {

    // create config
    PerfTestConfig config = JPerf.newConfigBuilder()
        .minThreads(1)
        .maxThreads(10)
        .duration(100)
        .testFactory(() -> new EmptyTest())
        .build();

    // run test
    JPerf.run(config);
  }

  public static class EmptyTest implements PerfTest {
    @Override
    public void setUp() throws Exception {

    }

    @Override
    public void test() throws Exception {

    }

    @Override
    public void tearDown() throws Exception {

    }
  }
}
