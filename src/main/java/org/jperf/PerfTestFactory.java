package org.jperf;

public interface PerfTestFactory {
  PerfTest createPerfTest() throws Exception;
}
