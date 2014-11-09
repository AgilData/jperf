package org.jperf;

public interface ResultWriter {

  void writeHeader(PerfTestConfig config) throws Exception;

  void writeResult(int threadCount, long duration, long samples) throws Exception;

  void close() throws Exception;
}
