package org.jperf;

public interface ConfigBuilder {
  ConfigBuilder minThreads(int minThreads);
  ConfigBuilder maxThreads(int maxThreads);
  ConfigBuilder threadIncrement(int threadIncrement);
  ConfigBuilder duration(int duration);
  ConfigBuilder testFactory(PerfTestFactory testFactory);
  ConfigBuilder resultWriter(ResultWriter resultWriter);
  void run() throws Exception;

  @Deprecated
  PerfTestConfig build();

}
