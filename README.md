JPerf is a simple performance and scalability testing framework for Java. Think of it as JUnit for performance tests.

JPerf is distributed under the Apache License 2.0 (see LICENSE.txt).

The latest version can be downloaded from https://github.com/andygrove/jperf/releases

The framework source code is in the 'src' directory and working examples are in the 'examples' directory.

Simply run 'ant' to compile the framework and run the examples (requires Apache Ant to be on the path).

JPerf can be used programatically or as a command-line tool with a configuration file.

To use JPerf programatically, create a class that implements the PerfTest interface:

    public class MyPerfTest implements PerfTest {

      public void setUp() {
        // do once per-thread setup here
      }

      public void test() {
        // do the operation here that you want to test
      }

      public void tearDown() {
        // do once per-thread teardown here
      }
    }

Then to run the test, write a main() method to run the test:

    public static void main(String arg[]) throws Exception {

    	PerfTestRunner r = new PerfTestRunner();
        r.setMinThread(1);
        r.setMaxThread(10);
        r.setTestPeriod(500);
        r.run(new PerfTestFactory() {
            @Override
            public PerfTest createPerfTest() throws Exception {
                return new MyPerfTest();
            }
        });

    }

PerfTestRunner will start with a single thread and then add more threads after running for testPeriod ms. Each
thread has a dedicated instance of the PerfTest class being tested and will call setUp() once and will then call
test() in a tight loop. At the end of the test, tearDown() will be called once.

As an alternative to writing programmatic tests, JPerf ships with some standard performance tests for Web and JDBC
that can be driven from the command-line with XML configuration files.

JDBC Example config:

    <jperf class="org.jperf.jdbc.JdbcPerfTestFactory"
       min-thread="1"
       max-thread="50"
       test-period="1000"
       stop-on-error="true">

        <param value="com.mysql.jdbc.Driver" />
        <param value="jdbc:mysql://localhost" />
        <param value="root" />
        <param value="" />
        <param value="SELECT * FROM test.foo" />
    </jperf>

HTTP example config:

    <jperf class="org.jperf.web.WebTestFactory" stop-on-error="false">
        <param value="http://localhost/create_customer.php" />
    </jperf>

Example output:

    jperf andy$ java -classpath 3rdparty/jdom/jdom.jar:dist/jperf-1.1.jar:../3rdparty/mysql-connector-j/mysql-connector-java-5.1.12-bin.jar org.jperf.Main examples/example-jdbc-config.xml
    [INFO] JPerf is testing class org.jperf.jdbc.JdbcPerfTestFactory
    [INFO]    1 test threads: 8,200 iterations/sec; avg iteration time: 0 ms ; 12,018 KB mem used; 2 active threads.
    [INFO]    2 test threads: 17,651 iterations/sec; avg iteration time: 0 ms ; 7,497 KB mem used; 3 active threads.
    [INFO]    3 test threads: 34,400 iterations/sec; avg iteration time: 0 ms ; 11,509 KB mem used; 4 active threads.
    [INFO]    4 test threads: 41,083 iterations/sec; avg iteration time: 0 ms ; 2,277 KB mem used; 5 active threads.
    [INFO]    5 test threads: 45,641 iterations/sec; avg iteration time: 0 ms ; 12,237 KB mem used; 6 active threads.
    [INFO]    6 test threads: 49,383 iterations/sec; avg iteration time: 0 ms ; 7,999 KB mem used; 7 active threads.
    [INFO]    7 test threads: 54,373 iterations/sec; avg iteration time: 0 ms ; 6,743 KB mem used; 8 active threads.
    [INFO]    8 test threads: 58,132 iterations/sec; avg iteration time: 0 ms ; 7,882 KB mem used; 9 active threads.
    [INFO]    9 test threads: 59,519 iterations/sec; avg iteration time: 0 ms ; 9,209 KB mem used; 10 active threads.
    [INFO]   10 test threads: 57,091 iterations/sec; avg iteration time: 0 ms ; 9,671 KB mem used; 11 active threads.
    [INFO] Waiting for 10 threads to finish
    [INFO] Writing results to /Users/andy/Development/jperf/JPerf-org.jperf.jdbc.JdbcPerfTestFactory.xml
    [INFO] Writing results to /Users/andy/Development/jperf/JPerf-org.jperf.jdbc.JdbcPerfTestFactory.csv

JPerf includes binaries from the following open source projects:

- Apache HttpComponents
- Apache Log4j v2
- JDOM
- SLF4J

Copyright (C) 2007-2014 Andy Grove.
