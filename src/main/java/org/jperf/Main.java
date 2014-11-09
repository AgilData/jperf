package org.jperf;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) throws Exception {

    if (args.length == 0) {
      System.out.println("Usage: jperf [config-file]");
      System.exit(-1);
    }

    try {

      SAXBuilder builder = new SAXBuilder();
      Document doc = builder.build(new FileInputStream(args[0]));
      Element root = doc.getRootElement();

      Class theClass = Class.forName(root.getAttributeValue("class"));

      List<Class> paramTypeList = new ArrayList<Class>();
      List<String> paramList = new ArrayList<String>();
      for (Object oParam : root.getChildren("param")) {
        Element elParam = (Element) oParam;
        //TODO: add support for other data types for factory ctor args
        paramTypeList.add(String.class);
        paramList.add(elParam.getAttributeValue("value"));
      }

      // convert lists to arrays
      Class[] argType = new Class[paramTypeList.size()];
      paramTypeList.toArray(argType);

      Object[] arg = new Object[paramList.size()];
      paramList.toArray(arg);

      Constructor ctor = theClass.getConstructor(argType);
      PerfTestFactory factory = (PerfTestFactory) ctor.newInstance(arg);

      PerfTestRunner r = new PerfTestRunner();
      r.setMinThread(getIntAttr(root, "min-thread", 1));
      r.setMaxThread(getIntAttr(root, "max-thread", 50));
      r.setTestPeriod(getIntAttr(root, "test-period", 500));
      r.setThreadIncrement(getIntAttr(root, "increment", 1));
      r.setStopThreadOnError(getBoolAttr(root, "stop-on-error", true));
      r.run(factory);

    } catch (Throwable th) {
      System.out.println("Failed: " + th.getMessage());
      System.err.println("Failed: " + th.getMessage());
      th.printStackTrace();
      System.exit(-1);
    } finally {
      System.exit(0);
    }

  }

  private static int getIntAttr(Element el, String name, int defaultValue) {
    String str = el.getAttributeValue(name);
    if (str == null || str.trim().length() == 0) {
      return defaultValue;
    }
    return Integer.parseInt(str);
  }

  private static boolean getBoolAttr(Element el, String name, boolean defaultValue) {
    String str = el.getAttributeValue(name);
    if (str == null || str.trim().length() == 0) {
      return defaultValue;
    }
    return Boolean.parseBoolean(str);
  }

  private static String getStringAttr(Element el, String name, String defaultValue) {
    String str = el.getAttributeValue(name);
    if (str == null || str.trim().length() == 0) {
      return defaultValue;
    }
    return str;
  }
}
