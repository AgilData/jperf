package org.jperf.ant;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.jperf.logger.JPerfLogger;
import org.jperf.PerfTestRunner;

import java.net.URLClassLoader;
import java.net.URL;
import java.io.File;

/**
 * @author Andy Grove
 */
public class JPerfTask extends Task implements JPerfLogger {

    /**
     * Class name of the performance test to run.
     */
    protected String test;

    /**
     * Classpath for the test classes.
     */
    protected String classpath;

    /**
     * Minimum number of threads.
     */
    protected int minThreads = 1;

    /**
     * Maximum number of threads.
     */
    protected int maxThreads = 10;

    /**
     * Thread increment.
     */
    protected int threadIncrement = 1;

    /**
     * Test duration.
     */
    protected int testDuration = 500;

    /**
     * File to write results to.
     */
    protected File resultFile;

    /**
     * Initialise the task.
     *
     * @throws BuildException
     */
    public void init() throws BuildException {
        super.init();
    }

    /**
     * Executes the performance test.
     *
     * @throws BuildException
     */
    public void execute() throws BuildException {
        super.execute();

        try {
            log( "JPerf is running perf test '" + test + "'" );

            if (classpath == null) {
                throw new BuildException( "No classpath specified" );
            }

            if (classpath.indexOf(';')>=0 || classpath.indexOf(':')>=0) {
                throw new BuildException( "Classpath must currently be a single jar file" );
            }

            File file = new File(classpath);
            URL[] urls = new URL[]{
                    new URL(file.toURL().toExternalForm())
            };
            URLClassLoader classLoader = new URLClassLoader(
                    urls,
                    this.getClass().getClassLoader()
            );
            Class testClass = classLoader.loadClass(test);
            if (testClass == null) {
                throw new BuildException( "Could not load class " + test );
            }

            if (resultFile == null) {
                resultFile = new File( "results.csv" );
            }

            PerfTestRunner r = new PerfTestRunner();
            r.setLogger( this );
            r.setMaxClient( 10 );
            r.setTestPeriod( 100 );
            r.setResultFilename( resultFile.getAbsolutePath() );
            r.run( testClass );

        } catch (BuildException be) {
            throw be;
        } catch (Exception e) {
            throw new BuildException( "JPerf failed to run test: " + e.getMessage(), e );
        }
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        log("Setting classpath to " + classpath);   
        this.classpath = classpath;
    }

    public void error(String message, Throwable th) {
        super.log( message + ": " + th.getClass() + ": " + th.getMessage() );
    }

    public void warn(String message) {
        super.log( "Warning! " + message );
    }

    public void info(String message) {
        super.log( message );
    }

    public void debug(String message) {
        super.log( message );
    }
}
