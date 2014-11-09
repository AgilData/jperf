package org.jperf;

public interface PerfTestFactory {
  PerfTest create() throws Exception;
}
