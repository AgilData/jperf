package org.jperf.example;

import org.jperf.PerfTest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Performance test for formatting a date using a static SimpleDateFormat that is shared
 * by all threads (with synchronized access).
 *
 * @author Andy Grove
 * @created 30-Aug-2007 20:11:09
 */
public class SynchronizedDateFormatTest implements PerfTest {

  protected static final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

  protected Date date = new Date();

  public void setUp() throws Exception {
    // no set up needed in this test
  }

  public void test() throws Exception {
    // SimpleDateFormat is not thread-safe so we need to synchronize access
    synchronized (df) {
      df.format(date);
    }
  }

  public void tearDown() throws Exception {
    // no tear down needed in this test
  }

}