package com.comcast.zucchini;

import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.Collections;

import java.io.IOException;

import cucumber.runtime.Glue;
import cucumber.runtime.Backend;
import cucumber.runtime.Runtime;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeGlue;
import cucumber.runtime.ClassFinder;
import cucumber.runtime.StopWatch;
import cucumber.runtime.ScenarioImpl;

import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberTagStatement;
import cucumber.api.StepDefinitionReporter;

import gherkin.I18n;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.DocString;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import gherkin.formatter.model.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends a wraps cucumber.runtime.Runtime object for Zucchini's extended functionality.
 *
 * @author Andrew Benton
 */
public class ZucchiniRuntime extends cucumber.runtime.Runtime {

    private static Logger logger = LoggerFactory.getLogger(ZucchiniRuntime.class);

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
     */
    public void addError(Throwable error) {
        super.addError(error);

        //if the error was not caused by a barrier timeout
        if(!(error instanceof ThreadDeath)) {
            TestContext tc = TestContext.getCurrent();
            AbstractZucchiniTest azt = tc.getParentTest();
            azt.failedContexts.add(tc);
            azt.flexBarrier.dec();
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

                order = azt.phase0.await();

                //reset the lock and scenario state
                if(order == 0) {
                    logger.debug("Beginning scenario: {}", statement.getVisualName());
                    //clear configuration here for per-scenario state
                    azt.failedContexts.clear();
                    azt.flexBarrier.refresh();
                }

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
    public void printSummary() {
        super.printSummary();
    }

    /**
     * {@inheritDoc}
     */
    public void buildBackendWorlds(Reporter reporter, Set<Tag> tags, Scenario gherkinScenario) {
        super.buildBackendWorlds(reporter, tags, gherkinScenario);
    }

    /**
     * {@inheritDoc}
     */
    public void disposeBackendWorlds(String scenarioDesignation) {
        super.disposeBackendWorlds(scenarioDesignation);
    }

    /**
     * {@inheritDoc}
     */
    public List<Throwable> getErrors() {
        return super.getErrors();
    }

    /**
     * {@inheritDoc}
     */
    public byte exitStatus() {
        return super.exitStatus();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getSnippets() {
        return super.getSnippets();
    }

    /**
     * {@inheritDoc}
     */
    public Glue getGlue() {
        return super.getGlue();
    }

    /**
     * {@inheritDoc}
     */
    public void runBeforeHooks(Reporter reporter, Set<Tag> tags) {
        super.runBeforeHooks(reporter, tags);
    }

    /**
     * {@inheritDoc}
     */
    public void runAfterHooks(Reporter reporter, Set<Tag> tags) {
        super.runAfterHooks(reporter, tags);
    }

    /**
     * {@inheritDoc}
     */
    public void runUnreportedStep(String featurePath,  I18n i18n, String stopKeyword, String stepName, int line, List<DataTableRow> dataTableRows, DocString docString) throws Throwable {
        super.runUnreportedStep(featurePath,  i18n, stopKeyword, stepName, line, dataTableRows, docString);
    }

    /**
     * {@inheritDoc}
     */
    public void runStep(String featurePath, Step step, Reporter reporter, I18n i18n) {
        super.runStep(featurePath, step, reporter, i18n);
    }
}
