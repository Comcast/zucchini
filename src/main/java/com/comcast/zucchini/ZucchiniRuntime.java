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

import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.Iterator;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.runtime.Backend;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeGlue;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.StopWatch;

import cucumber.runtime.model.CucumberScenario;
import cucumber.runtime.model.CucumberExamples;
import cucumber.runtime.model.CucumberScenarioOutline;

import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberTagStatement;
import cucumber.api.StepDefinitionReporter;

import gherkin.I18n;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Step;

/**
 * Extends a wraps {@link cucumber.runtime.Runtime} object for Zucchini's extended functionality.
 *
 * @author Andrew Benton
 */
public class ZucchiniRuntime extends cucumber.runtime.Runtime {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZucchiniRuntime.class);

    protected ResourceLoader rl;
    protected ClassLoader cl;
    protected ZucchiniRuntimeOptions ros;

    private static Object summaryLock = new Object();

    /**
     * Creates a sub-zucchini runtime replacing the {@link RuntimeOptions} with {@link ZucchiniRuntimeOptions}, and then links it for all inherited calls.
     *
     * @param resourceLoader
     * @param classFinder
     * @param classLoader
     * @param runtimeOptions RuntimeOptions that will be converted to {@link ZucchiniRuntimeOptions} if need be, and then used
     */
    public ZucchiniRuntime(ResourceLoader resourceLoader, ClassFinder classFinder, ClassLoader classLoader, RuntimeOptions runtimeOptions) {
        super(resourceLoader, classFinder, classLoader, runtimeOptions);
        if(runtimeOptions instanceof ZucchiniRuntimeOptions)
            this.ros = (ZucchiniRuntimeOptions)runtimeOptions;
        else
            this.ros = new ZucchiniRuntimeOptions(runtimeOptions);
        this.cl = classLoader;
        this.rl = resourceLoader;
    }

    /**
     * Creates a sub-zucchini runtime replacing the {@link RuntimeOptions} with {@link ZucchiniRuntimeOptions}, and then links it for all inherited calls.
     *
     * @param resourceLoader
     * @param classLoader
     * @param backends
     * @param runtimeOptions RuntimeOptions that will be converted to {@link ZucchiniRuntimeOptions} if need be, and then used
     */
    public ZucchiniRuntime(ResourceLoader resourceLoader, ClassLoader classLoader, Collection<? extends Backend> backends, RuntimeOptions runtimeOptions) {
        super(resourceLoader, classLoader, backends, runtimeOptions);
        if(runtimeOptions instanceof ZucchiniRuntimeOptions)
            this.ros = (ZucchiniRuntimeOptions)runtimeOptions;
        else
            this.ros = new ZucchiniRuntimeOptions(runtimeOptions);
        this.cl = classLoader;
        this.rl = resourceLoader;
    }

    /**
     * Creates a sub-zucchini runtime replacing the {@link RuntimeOptions} with {@link ZucchiniRuntimeOptions}, and then links it for all inherited calls.
     *
     * @param resourceLoader
     * @param classLoader
     * @param backends
     * @param runtimeOptions RuntimeOptions that will be converted to {@link ZucchiniRuntimeOptions} if need be, and then used
     * @param optionalGlue
     */
    public ZucchiniRuntime(ResourceLoader resourceLoader, ClassLoader classLoader, Collection<? extends Backend> backends, RuntimeOptions runtimeOptions, RuntimeGlue optionalGlue) {
        super(resourceLoader, classLoader, backends,  runtimeOptions, optionalGlue);
        if(runtimeOptions instanceof ZucchiniRuntimeOptions)
            this.ros = (ZucchiniRuntimeOptions)runtimeOptions;
        else
            this.ros = new ZucchiniRuntimeOptions(runtimeOptions);
        this.cl = classLoader;
        this.rl = resourceLoader;
    }

    /**
     * Creates a sub-zucchini runtime replacing the {@link RuntimeOptions} with {@link ZucchiniRuntimeOptions}, and then links it for all inherited calls.
     *
     * @param resourceLoader
     * @param classLoader
     * @param backends
     * @param runtimeOptions RuntimeOptions that will be converted to {@link ZucchiniRuntimeOptions} if need be, and then used
     * @param stopWatch
     * @param optionalGlue
     */
    public ZucchiniRuntime(ResourceLoader resourceLoader, ClassLoader classLoader, Collection<? extends Backend> backends, RuntimeOptions runtimeOptions, StopWatch stopWatch, RuntimeGlue optionalGlue) {
        super(resourceLoader, classLoader, backends, runtimeOptions, stopWatch, optionalGlue);
        if(runtimeOptions instanceof ZucchiniRuntimeOptions)
            this.ros = (ZucchiniRuntimeOptions)runtimeOptions;
        else
            this.ros = new ZucchiniRuntimeOptions(runtimeOptions);
        this.cl = classLoader;
        this.rl = resourceLoader;
    }

    /**
     * Similar to the inherited version, but adds a check for ThreadDeath from the Barrier.sync().
     *
     * @param error The error that was thrown by the test and must be registered.  If it wasn't a {@link ThreadDeath}, then this will be checked against a list of already failed contexts.
     */
    @Override
    public void addError(Throwable error) {
        super.addError(error);

        TestContext tc = TestContext.getCurrent();

        //if the error was not caused by a barrier timeout
        if(!(error instanceof ThreadDeath)) {
            AbstractZucchiniTest azt = tc.getParentTest();
            if(azt.canBarrier()) {
                if(!azt.failedContexts.contains(tc)) {
                    synchronized(azt.failedContexts) {
                        if(!azt.failedContexts.contains(tc)) {
                            azt.failedContexts.add(tc);
                            azt.flexBarrier.dec();
                        }
                    }
                }
            }
        }
    }

    private static void lockResetLockRun(AbstractZucchiniTest azt, Formatter f, Reporter r, boolean parallel, boolean barrierEnabled, ZucchiniRuntime rt, CucumberTagStatement cs) {
        if(barrierEnabled) {
            int order = -1; //invalid order

            if(parallel)
                order = azt.phase0.await();
            else
                order = 0;

            if(order == 0) {
                azt.failedContexts.clear();
                azt.flexBarrier.refresh();
            }

            if(parallel)
                order = azt.phase1.await();
        }

        cs.run(f, r, rt);
    }

    /**
     * This replaces the function of the cucumber.runtime.model.CucumberFeature so that it can handle barrier syncs between scenarios and prevent odd behavior based on the actions of previous scenarios.
     */
    @Override
    public void run() throws IOException {
        Formatter formatter = this.ros.formatter(this.cl);
        Reporter reporter = this.ros.reporter(this.cl);

        StepDefinitionReporter sdr = this.ros.stepDefinitionReporter(this.cl);

        this.getGlue().reportStepDefinitions(sdr);
        TestContext tc = TestContext.getCurrent();
        AbstractZucchiniTest azt = tc.getParentTest();

        Iterator<CucumberFeatureHolder> features = azt.fastrunIteratorFactory(this.ros.cucumberFeatures(this.rl).iterator());

        boolean parallel = azt.isParallel();
        boolean barrierEnabled = azt.canBarrier();

        CucumberFeatureHolder lastCFH = null;

        while (true) {
            CucumberFeatureHolder cfh = null;
            try {
                cfh = features.next();
            } catch (NoSuchElementException ee) {
                break;
            }

            CucumberFeature cf = cfh.getFeature();
            CucumberTagStatement statement = cfh.getStatement();

            if(null == lastCFH || lastCFH.getFeature() != cf) {
                /* Setup a new feature IF the feature is different than the last time */
                if (null != lastCFH) {
                    /* Send the eof of a feature as we are on a new feature file */
                    formatter.eof();
                }
                formatter.uri(cf.getPath());
                formatter.feature(cf.getGherkinFeature());
            }

            if(null != statement) {
                if(statement instanceof CucumberScenarioOutline) {
                    CucumberScenarioOutline cso = (CucumberScenarioOutline)statement;
                    cso.formatOutlineScenario(formatter);
                    for(CucumberExamples cucumberExamples : cso.getCucumberExamplesList()) {
                        cucumberExamples.format(formatter);
                        for(CucumberScenario cs : cucumberExamples.createExampleScenarios()) {
                            ZucchiniRuntime.lockResetLockRun(azt, formatter, reporter, parallel, barrierEnabled, this, cs);
                        }
                    }
                }
                else {
                    ZucchiniRuntime.lockResetLockRun(azt, formatter, reporter, parallel, barrierEnabled, this, statement);
                }
            }

            lastCFH = cfh;
        }
        /* Send the eof of a feature file as this hasn't been called on the last CucumberFeature */
        if (null != lastCFH) {
            formatter.eof();
        }

        formatter.done();
        formatter.close();
        synchronized (summaryLock) {
            /* Synchronize the summary printing as
             * the Cucumber printers were not written to
             * be called by multiple threads and the summaries
             * will not print properly
             */
            this.printSummary();
        }
     }

     /**

    }

    /**
     * This encapsulates the {@link cucumber.runtime.Runtime#runStep} method with a level of safety, and provides information back to the barrier about whether it is safe to kill the running thread.
     */
    @Override
    public void runStep(String featurePath, Step step, Reporter reporter, I18n i18n) {
        LOGGER.trace(String.format("Running step from ZucchiniRuntime: [%s, %s, %s, %s]", featurePath.toString(), step.toString(), reporter.toString(), i18n.toString()));

        TestContext tc = TestContext.getCurrent();

        try {
            tc.canKill = true;
            super.runStep(featurePath, step, reporter, i18n);
            tc.canKill = false;
        }
        catch(Throwable t) {
            tc.canKill = false;
            this.addError(t);
        }
    }
}
