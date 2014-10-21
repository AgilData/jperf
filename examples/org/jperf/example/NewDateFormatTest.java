package org.jperf.example;

import org.jperf.PerfTest;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Performance test for formatting a date using a new SimpleDateFormat each time.
 *
 * @author Andy Grove
 */
public class NewDateFormatTest implements PerfTest {

  protected Date date = new Date();

  public void setUp() throws Exception {
    // no set up needed in this test
  }

  public void test() throws Exception {
    // create a local SimpleDateFormat each time
    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
    df.format(date);
  }

  public void tearDown() throws Exception {
    // no tear down needed in this test
  }
}
