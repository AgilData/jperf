package org.jperf;

import com.google.common.base.Throwables;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread that repeatedly invokes the PerfTest in a tight loop and updates the shared counter after each
 * invocation.
 */
public class SingleTestRunner implements Runnable {

  /** The test to invoke. */
  protected final PerfTest test;

  /** Counter to track number of invocations. */
  protected final AtomicLong counter = new AtomicLong();

  /** Flag to indicate if test is alive or not. */
  protected final AtomicBoolean alive = new AtomicBoolean(true);

  /** Create a perf thread for the specified test instance. */
  public SingleTestRunner(PerfTest test) {
    this.test = test;
  }

  /**
   * This is the main testing loop. The setup() method is called once and then the test method is
   * called in a tight loop until the test runner calls the stop() method, then the test tearDown()
   * method is invoked.
   */
  public void run() {
    try {
      test.setUp();
      while (alive.get()) {
        test.test();
        counter.incrementAndGet();
      }
    } catch (Exception e) {
      throw Throwables.propagate(e);
    } finally {
      try {
        test.tearDown();
      } catch (Exception e) {
        // ignore errors in tearDown for now
      }
    }
  }

  /** Terminate the thread. */
  public void stop() {
    alive.set(false);
  }

  /** Get reference to the counter. */
  public AtomicLong getCounter() {
    return counter;
  }
}
