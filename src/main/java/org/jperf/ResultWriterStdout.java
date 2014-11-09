package org.jperf;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

public class ResultWriterStdout implements ResultWriter {

  final DecimalFormat formatter = new DecimalFormat("#,##0");

  @Override
  public void writeHeader(PerfTestConfig config) throws Exception {
    System.out.println(String.format("Running on %s with config: %s",
        DateFormat.getDateTimeInstance().format(new Date()),
        config
    ));

  }

  @Override
  public void writeResult(int threadCount, long duration, long samples) throws Exception {
    System.out.println(String.format("Threads: %s: Samples: %s; Duration: %s; Throughput: %s",
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
