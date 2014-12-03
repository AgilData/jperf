package org.jperf;

import com.google.common.base.Throwables;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class ResultWriterCSV implements ResultWriter {

  final DecimalFormat formatter = new DecimalFormat("0");

  private FileOutputStream os;

  public ResultWriterCSV() {
  }

  public ResultWriterCSV(File file) throws IOException {
    System.out.println("Writing results to " + file.getAbsolutePath());
    this.os = new FileOutputStream(file);
  }

  @Override
  public void writeHeader(PerfTestConfig config) throws Exception {
    String header = "Threads,Samples,Duration,Throughput";
    System.out.println(header);
    if (os != null) {
      os.write((header+"\n").getBytes());
      os.flush();
    }
  }

  @Override
  public void writeResult(int threadCount, long duration, long samples) throws Exception {
    String line = String.format("%s,%s,%s,%s",
        formatter.format(threadCount),
        formatter.format(samples),
        formatter.format(duration),
        formatter.format(samples * 1000.0f / duration)
    );
    System.out.println(line);
    if (os != null) {
      os.write((line + "\n").getBytes());
      os.flush();
    }
  }

  @Override
  public void close() {
    if (os != null) {
      try {
        os.close();
      } catch (IOException e) {
        throw Throwables.propagate(e);
      }
    }
  }
}
