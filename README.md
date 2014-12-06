JPerf is a simple performance and scalability testing framework for Java. Think of it as JUnit for performance tests.

JPerf is distributed under the Apache License 2.0 (see LICENSE.txt).

The latest version can be downloaded from https://github.com/codefutures/jperf/releases

JPerf can be used programmatically or from the command line to run scalability tests again any class 
implementing the PerfTest interface.

Programmatic use:

```java
// create config
PerfTestConfig config = JPerf.newConfigBuilder()
    .minThreads(1)
    .maxThreads(10)
    .duration(100)
    .testFactory(() -> new EmptyTest())
    .build();

// run test
JPerf.run(config);
```

Command-line use:

```
usage: jperf [-class <arg>] [-duration <arg>] [-increment <arg>] [-max
       <arg>] [-min <arg>]
 -class <arg>       Name of class that implemented org.jperf.PerfTest
 -duration <arg>    The duration in milliseconds (per thread level)
 -increment <arg>   The number of threads to increment by
 -max <arg>         The maximum number of threads to test with
 -min <arg>         The number of threads to start testing with

All arguments are optional, except for 'class'.
```

Example of command line use:

```
java -classpath yourclasspathhere org.jperf.JPerf -class org.jperf.noop.NoOpTest
```

In either case, output similar to the following will be written to stdout.

```
Running on Nov 9, 2014 11:15:39 AM with config: PerfTestConfig{minThreads=1, maxThreads=10, threadIncrement=1, duration=1000}
With 1 threads there were 200,382,238 samples
With 2 threads there were 390,074,461 samples
With 3 threads there were 304,151,933 samples
With 4 threads there were 277,960,408 samples
With 5 threads there were 231,347,787 samples
With 6 threads there were 180,998,431 samples
With 7 threads there were 172,647,235 samples
With 8 threads there were 145,501,665 samples
With 9 threads there were 135,152,189 samples
With 10 threads there were 124,737,834 samples
Stopping threads
Finished
```

JPerf has dependencies on the following open source projects:

- Apache CLI
- Google Guava

Copyright (C) 2007-2014 CodeFutures
