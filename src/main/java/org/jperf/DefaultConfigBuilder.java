package org.jperf;

public class DefaultConfigBuilder implements ConfigBuilder {
  int minThreads = 1;
  int maxThreads = 10;
  int threadIncrement = 10;
  int duration = 1000;
  ResultWriter resultWriter;
  PerfTestFactory testFactory;

  protected final PerfTestConfig config = new PerfTestConfig();

  public DefaultConfigBuilder() {
  }

  @Override
  public DefaultConfigBuilder minThreads(int minThreads) {
    config.minThreads = minThreads;
    return this;
  }

  @Override
  public DefaultConfigBuilder maxThreads(int maxThreads) {
    config.maxThreads = maxThreads;
    return this;
  }

  @Override
  public ConfigBuilder threadIncrement(int threadIncrement) {
    config.threadIncrement = threadIncrement;
    return this;
  }

  @Override
  public DefaultConfigBuilder duration(int duration) {
    config.duration = duration;
    return this;
  }

  @Override
  public DefaultConfigBuilder testFactory(PerfTestFactory testFactory) {
    config.testFactory = testFactory;
    return this;
  }

  @Override
  public ConfigBuilder resultWriter(ResultWriter resultWriter) {
    config.resultWriter = resultWriter;
    return this;
  }

  @Override
  public PerfTestConfig build() {
    return config;
  }
}
