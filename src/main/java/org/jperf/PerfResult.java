package org.jperf;

/**
 * A single container for a performance result.
 *
 * @author Andy Grove
 */
public class PerfResult {

  /**
   * Number of test threads.
   */
  private int testThreadCount;

  /**
   * Total number of active threads.
   */
  private int activeThreadCount;

  /**
   * Throughput (test iterations per second).
   */
  private float throughput;

  private long memFree, memMax, memTotal;
  private long iterations, time;
  private long testCreate, testSetup;

  public PerfResult(int testThreadCount, long transactions, long time, float throughput, long memFree, long memMax, long memTotal, int activeThreadCount) {
    this.iterations = transactions;
    this.time = time;
    this.testThreadCount = testThreadCount;
    this.throughput = throughput;
    this.memFree = memFree;
    this.memMax = memMax;
    this.memTotal = memTotal;
    this.activeThreadCount = activeThreadCount;
  }

  public int getTestThreadCount() {
    return testThreadCount;
  }

  public float getThroughput() {
    return throughput;
  }

  public long getMemFree() {
    return memFree;
  }

  public long getMemMax() {
    return memMax;
  }

  public long getMemTotal() {
    return memTotal;
  }

  public long getIterations() {
    return iterations;
  }

  public long getTime() {
    return time;
  }

  public long getTestCreate() {
    return testCreate;
  }

  public void setTestCreate(long testCreate) {
    this.testCreate = testCreate;
  }

  public long getTestSetup() {
    return testSetup;
  }

  public void setTestSetup(long testSetup) {
    this.testSetup = testSetup;
  }

  public int getActiveThreadCount() {
    return activeThreadCount;
  }

  public void setActiveThreadCount(int activeThreadCount) {
    this.activeThreadCount = activeThreadCount;
  }
}
