package org.jperf.util;

/**
 * @author Andy Grove
 */
public class Timer {

  long startTime;
  long total = 0;

  public void start() {
    startTime = System.currentTimeMillis();
  }

  public void stop() {
    total += System.currentTimeMillis() - startTime;
  }

  public void reset() {
    total = 0;
  }

  public long value() {
    return total;
  }

}
