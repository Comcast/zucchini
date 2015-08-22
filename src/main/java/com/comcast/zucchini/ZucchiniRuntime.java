package com.comcast.zucchini;

import java.util.List;
import java.util.Set;
import java.util.Collection;

import java.io.IOException;

import cucumber.runtime.Glue;
import cucumber.runtime.Backend;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeGlue;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.StopWatch;

import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberTagStatement;
import cucumber.api.StepDefinitionReporter;

import gherkin.I18n;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.DocString;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;

/**
 * Extends a wraps cucumber.runtime.Runtime object for Zucchini's extended functionality.
 *
 * @author Andrew Benton
 */
public class ZucchiniRuntime extends cucumber.runtime.Runtime {

    protected ResourceLoader rl;
    protected ClassLoader cl;
    protected ZucchiniRuntimeOptions ros;

    /**
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * {@inheritDoc}
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
     * @param error The error that was thrown by the test and must be registered.  If it wasn't a {@see ThreadDeath}, then this will be checked against a list of already failed contexts.
     */
    @Override
    public void addError(Throwable error) {
        super.addError(error);

        TestContext tc = TestContext.getCurrent();

        //if the error was not caused by a barrier timeout
        if(!(error instanceof ThreadDeath)) {
            AbstractZucchiniTest azt = tc.getParentTest();
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

    /**
     * This is a shortcut to extract the name from the TestContext.
     */
    private static String name() {
        TestContext tc = TestContext.getCurrent();
        if(tc != null)
            return tc.name();
        else
            return "<NULL>";
    }

    /**
     * This replaces the function of the cucumber.runtime.model.CucumberFeature so that it can handle barrier syncs between scenarios and preven odd behavior based on the actions of previous scenarios.
     */
    @Override
    public void run() throws IOException {
        List<CucumberFeature> features = this.ros.cucumberFeatures(this.rl);

        Formatter formatter = this.ros.formatter(this.cl);
        Reporter reporter = this.ros.reporter(this.cl);

        StepDefinitionReporter sdr = this.ros.stepDefinitionReporter(this.cl);

        this.getGlue().reportStepDefinitions(sdr);
        TestContext tc = TestContext.getCurrent();
        AbstractZucchiniTest azt = tc.getParentTest();

        int order = 0;

        for(CucumberFeature cf : features) {
            formatter.uri(cf.getPath());
            formatter.feature(cf.getGherkinFeature());

            for(CucumberTagStatement statement : cf.getFeatureElements()) {

                if(azt.isParallel())
                    order = azt.phase0.await();
                else
                    order = 0;

                //reset the lock and scenario state
                if(order == 0) {
                    //clear configuration here for per-scenario state
                    azt.failedContexts.clear();
                    azt.flexBarrier.refresh();
                }

                if(azt.isParallel())
                    order = azt.phase1.await();

                statement.run(formatter, reporter, this);
            }

            formatter.eof();
        }

        formatter.done();
        formatter.close();
        this.printSummary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printSummary() {
        super.printSummary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void buildBackendWorlds(Reporter reporter, Set<Tag> tags, Scenario gherkinScenario) {
        super.buildBackendWorlds(reporter, tags, gherkinScenario);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeBackendWorlds(String scenarioDesignation) {
        super.disposeBackendWorlds(scenarioDesignation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Throwable> getErrors() {
        return super.getErrors();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte exitStatus() {
        return super.exitStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSnippets() {
        return super.getSnippets();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Glue getGlue() {
        return super.getGlue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runBeforeHooks(Reporter reporter, Set<Tag> tags) {
        super.runBeforeHooks(reporter, tags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runAfterHooks(Reporter reporter, Set<Tag> tags) {
        super.runAfterHooks(reporter, tags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void runUnreportedStep(String featurePath,  I18n i18n, String stopKeyword, String stepName, int line, List<DataTableRow> dataTableRows, DocString docString) throws Throwable {
        super.runUnreportedStep(featurePath,  i18n, stopKeyword, stepName, line, dataTableRows, docString);
    }

    /**
     * This encapsulates the {@link Runtime#runStep} method with a level of safety, and provides information back to the barrier about whether it is safe to kill the running thread.
     */
    @Override
    public void runStep(String featurePath, Step step, Reporter reporter, I18n i18n) {
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
