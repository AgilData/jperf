package org.jperf.web;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jperf.PerfTest;

import java.io.InputStream;

public class WebPerfTest implements PerfTest {

  private HttpClient httpclient;

  private String url;

  public WebPerfTest(String url) {
    this.url = url;
  }

  @Override
  public void setUp() throws Exception {
    httpclient = new DefaultHttpClient();
  }

  @Override
  public void test() throws Exception {
    HttpGet httpget = new HttpGet(url);
    HttpResponse response = httpclient.execute(httpget);
    int statusCode = response.getStatusLine().getStatusCode();
    if (200 != statusCode) {
      throw new RuntimeException("Request failed with HTTP status code " + statusCode);
    }
    HttpEntity entity = response.getEntity();
    if (entity != null) {
      InputStream instream = entity.getContent();
      int l;
      byte[] tmp = new byte[2048];
      while ((l = instream.read(tmp)) != -1) {
      }
    }
  }

  @Override
  public void tearDown() throws Exception {
  }
}
