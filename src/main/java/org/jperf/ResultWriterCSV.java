package org.jperf;

import java.text.DecimalFormat;

public class ResultWriterCSV implements ResultWriter {

  final DecimalFormat formatter = new DecimalFormat("0");

  @Override
  public void writeHeader(PerfTestConfig config) throws Exception {
    System.out.println("Threads,Samples,Duration,Throughput");
  }

  @Override
  public void writeResult(int threadCount, long duration, long samples) throws Exception {
    System.out.println(String.format("%s,%s,%s,%s",
        formatter.format(threadCount),
        formatter.format(samples),
        formatter.format(duration),
        formatter.format(samples * 1000.0f / duration)
    ));
  }

  @Override
  public void close() {
  }
}
