package org.jperf;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.net.URLClassLoader;
import java.net.URL;

/**
 * @author Andy Grove
 */
public class PerfTask extends Task {

    private List<Classpath> classpaths = new ArrayList<Classpath>();
    private List<Test> tests = new ArrayList<Test>();

    public void execute() throws BuildException {

        URLClassLoader classLoader = null;

        try {
            // set up classpath
            URL url[] = new URL[ classpaths.size() ];
            for (int i = 0; i < url.length; i++) {
                url[i] = new File( classpaths.get(i).getPath() ).toURL();
            }
            classLoader = new URLClassLoader( url, this.getClass().getClassLoader() );
        }
        catch (Exception e) {
            error(e);
            throw new BuildException( "Error in classpath", e );
        }

        for (Test test: tests) {
            log( "Running JPerf Test: " + test.getClassName() );
            try {

                Class theClass = classLoader.loadClass( test.getClassName() );

                PerfTestRunner runner = new PerfTestRunner();
                runner.setMaxClient( test.getMaxClient() );
                runner.setTestPeriod( test.getTestPeriod() );
                runner.run( theClass );
            } catch (Exception e) {
                error(e);
                throw new BuildException( "JPerf failed: " + e.getClass() + ": " + e.getMessage(), e );
            }
        }

    }

    private void error(Exception e) {
        log( e.getClass().getName() + ": " + e.getMessage());
    }

    public Test createTest() {
        Test test = new Test();
        tests.add( test );
        return test;
    }

    public Classpath createClasspath() {
        final Classpath cp = new Classpath();
        classpaths.add( cp );
        return cp;
    }

    
    public class Classpath {
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public class Test {
        private String className;
        private int maxClient = 10;
        private int testPeriod = 100;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public int getMaxClient() {
            return maxClient;
        }

        public void setMaxClient(int maxClient) {
            this.maxClient = maxClient;
        }

        public int getTestPeriod() {
            return testPeriod;
        }

        public void setTestPeriod(int testPeriod) {
            this.testPeriod = testPeriod;
        }
    }
}
