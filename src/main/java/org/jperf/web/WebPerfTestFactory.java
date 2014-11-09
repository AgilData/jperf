package org.jperf.web;

import org.jperf.PerfTest;
import org.jperf.PerfTestFactory;

/**
 * Simple web performance test. Performs HTTP GET operation on provided URL and reads response. If status code
 * is not 200 then an exception is thrown.
 */
public class WebPerfTestFactory implements PerfTestFactory {

  private String url;

  public WebPerfTestFactory(String url) {
    this.url = url;
  }

  @Override
  public PerfTest createPerfTest() throws Exception {
    return new WebPerfTest(url);
  }
}
