/**
 * Copyright 2014 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.zucchini;

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import net.masterthought.cucumber.ReportBuilder;

import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.testng.Assert
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test

import gherkin.formatter.Formatter;


/**
 * Constructs a suite of Cucumber tests for every TestContext as returned by the
 * {@link #getTestContexts()} method. This should be used when working with either external
 * hardware or a virtual device (like a browser) to run the same cucumber tests but against
 * a different test target.
 * 
 * To do this correctly, each step ("given", "when" or "then") should get access to the object
 * under test by calling {@link TestContext.getCurrent()}.
 * 
 * @author Clark Malmgren
 */
abstract class AbstractZucchiniTest {

    private static Logger logger = LoggerFactory.getLogger(AbstractZucchiniTest.class)
    private List features = []
    private TestNGZucchiniRunner runner;

    @AfterClass
    public void generateReports() {
        /* Determine Output File Location */
        ZucchiniOutput options = getClass().getAnnotation(ZucchiniOutput)
        File json = new File(options ? options.json() : "target/zucchini.json")
        
        /* Write the "pretty" output */
        def writer = new FileWriter(json)
        writer << new JsonBuilder(features).toPrettyString()
        writer.close()
        
        /* Generate the Results */
        File html = new File(options ? options.html() : "target/zucchini-reports")
        def reportBuilder = new ReportBuilder([ json.absolutePath ], html, "", "1", "Zucchini", true, true, true, false, false, "", false);
        reportBuilder.generateReports();

        boolean buildResult = reportBuilder.getBuildStatus();
        if (!buildResult) {
            throw new MojoExecutionException("BUILD FAILED - Check Report For Details");
        }
    }
    
    @Test
    public void run() {
        List<TestContext> contexts = getTestContexts()
        isParallel() ? runParallel(contexts) : runSerial(contexts)
    }
    
    void runParallel(List<TestContext> contexts) {
        List<Thread> threads = []
        int failures = 0
        
        contexts.each { TestContext context ->
            threads.push(Thread.start(context.name) {
                failures += runWith(context) ? 0 : 1
            })
        }
        
        threads.each { it.join() }
        
        Assert.assertEquals(failures, 0, "There were ${failures} executions against a TestContext")
    }
    
    void runSerial(List<TestContext> contexts) {
        int failures = 0

        contexts.each {
            failures += runWith(it) ? 0 : 1
        }

        Assert.assertEquals(failures, 0, "There were ${failures} executions against a TestContext")
    }
    
    /**
     * Run all configured cucumber features and scenarios against the given TestContext.
     * 
     * @param context the test context
     * @return true if successful, otherwise false
     */
    boolean runWith(TestContext context) {
        TestContext.setCurrent(context)

        logger.debug("ZucchiniTest[${context.name}] starting")
        def runner = new TestNGZucchiniRunner(getClass())
        
        try {
            setup(context);
            setupFormatter(context, runner);
            runner.runCukes();
            return true;
        } catch (Throwable t) {
            t.printStackTrace()
            return false
        } finally {
            logger.debug("ZucchiniTest[${context.name}] finished")
            
            synchronized(features) {
                def results = new JsonSlurper().parseText(runner.getJSONOutput())
                features.addAll(results.collect {
                    it.id = "--zucchini--${context.name}-${it.id}"
                    it.uri = "--zucchini--${context.name}-${it.uri}"
                    it.name = "ZucchiniTestContext[${context.name}]:: ${it.name}"
                    return it
                })
            }
            
            cleanup(context)
            TestContext.removeCurrent()
        }
    }

    /**
     * If this returns true, all cucumber features will run against each TestContext in parallel, otherwise
     * they will run one after the other (in order). Override this method to change the output.
     * 
     * <b>The default value is <code>true</code> so the default behavior is parallel execution.</b>
     */
    public boolean isParallel() {
        return true
    }

    /**
     * Returns the full list of objects to test against. The full suite of cucumber features
     * and scenarios will be run against these object in parallel.
     * 
     * @return the full list of objects to test against.
     */
    public abstract List<TestContext> getTestContexts();

    /**
     * Optionally override this method to do custom cleanup for the object under test
     * 
     * @param out the object under test to cleanup
     */
    public void cleanup(TestContext out) {
        logger.debug("Cleanup method was not implemented for ${out}")
    }
	
	/**
	 * Optionally override this method to do custom setup for the object under test
	 *
	 * @param out the object under test to setup
	 */
    public void setup(TestContext out) {
        logger.debug("Setup method was not implemented for ${out}")
    }
    
    /**
     * Configures formatter(s) of your choosing or overrides their default behavior
     * @param out The object under test
     * @param runner The object used for test execution
     * To modify formatters, a sample such as the one below should be used:
     * <pre>
     * List<Formatter> formatters = runner.getFormatters();
     * for (Formatter formatter : formatters) {
     *   if (formatter instanceof YourCustomFormatter) {
     *     ((YourCustomFormatter)formatter).yourFormatterModifierMethod();
     *   }
     * }
     * 
     * </pre>
     */
    public void setupFormatter(TestContext out, TestNGZucchiniRunner runner) {
        logger.debug("Setup formatter method was not implemented for ${out}");
    }
}
