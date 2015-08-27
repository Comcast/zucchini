package com.comcast.zucchini;

import java.util.List;

import java.lang.reflect.Proxy;

import cucumber.api.SnippetType;
import cucumber.api.StepDefinitionReporter;
import cucumber.api.SummaryPrinter;

import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.Env;
import cucumber.runtime.formatter.PluginFactory;
import cucumber.runtime.model.CucumberFeature;

import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;

/**
 * This is a thin wrapper around RuntimeOptions that allows injection of the {@link ZucchiniInvocationHandler} for future extension of the {@link ZucchiniRuntime}
 *
 * @author Andrew Benton
 */
public class ZucchiniRuntimeOptions extends RuntimeOptions {
    private RuntimeOptions ros;

    /**
     * Creates a ZucchiniRuntimeOptions from a string of arguments.
     *
     * @param argv The string of arguments
     */
    public ZucchiniRuntimeOptions(String argv) {
        super(argv);
        this.ros = this;
    }

    /**
     * Creates ZucchiniRuntimeOptions from a list of arguments.
     *
     * @param argv The list of arguments, already tokenized
     */
    public ZucchiniRuntimeOptions(List<String> argv) {
        super(argv);
        this.ros = this;
    }

    /**
     * Creates a ZucchiniRuntimeOptions from an {@link Env} and a list of arguments.
     *
     * @param env The environment to use
     * @param argv Tokenized list of arguments to use
     */
    public ZucchiniRuntimeOptions(Env env, List<String> argv) {
        super(env, argv);
        this.ros = this;
    }

    /**
     * Creates a ZucchiniRuntimeOptions from a {@link PluginFactory} and a list of arguments.
     *
     * @param pluginFactory The pre-created factory that is used to instantiate plugins internally
     * @param argv The pre-tokenized list of arguments to use
     */
    public ZucchiniRuntimeOptions(PluginFactory pluginFactory, List<String> argv) {
        super(pluginFactory, argv);
        this.ros = this;
    }

    /**
     * Creates a ZucchiniRuntimeOptions from a {@link Env}, a {@link PluginFactory}, and a list of arguments
     *
     * @param env Environment to create the runtime options in
     * @param pluginFactory The pre-created factory that is used to instantiate plugins internally
     * @param argv The tokenized list of arguments
     */
    public ZucchiniRuntimeOptions(Env env, PluginFactory pluginFactory, List<String> argv) {
        super(env, pluginFactory, argv);
        this.ros = this;
    }

    /**
     * Creates a ZucchiniRuntimeOptions from a {@link RuntimeOptions}.
     *
     * For this operation, all inherited methods punch through to the passed runtime options, and overridden access to the pluginProxy does internal access here before reaching the underlying RuntimeOptions
     *
     * @param ros The RuntimeOptions that is used for most calls.
     */
    public ZucchiniRuntimeOptions(RuntimeOptions ros) {
        super(""); //wasted option
        this.ros = ros;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CucumberFeature> cucumberFeatures(ResourceLoader resourceLoader) {
        return this.ros.cucumberFeatures(resourceLoader);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Formatter formatter(ClassLoader classLoader) {
        return pluginProxy(classLoader, Formatter.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reporter reporter(ClassLoader classLoader) {
        return pluginProxy(classLoader, Reporter.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StepDefinitionReporter stepDefinitionReporter(ClassLoader classLoader) {
        return pluginProxy(classLoader, StepDefinitionReporter.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SummaryPrinter summaryPrinter(ClassLoader classLoader) {
        return pluginProxy(classLoader, SummaryPrinter.class);
    }

    /**
     * This changes the pluginProxy so that the proxy that it returns serializes calls.
     *
     * A future goal for this is to serialize calls to the underlying object types for environments with excessive numbers of contexts.
     *
     * {@inheritDoc}
     */
    @Override
    public <T> T pluginProxy(ClassLoader classLoader, final Class<T> type) {
        Object baseProxy = this.ros.pluginProxy(classLoader, type);

        Object proxy = Proxy.newProxyInstance(classLoader, new Class<?>[]{type}, new ZucchiniInvocationHandler(baseProxy, type));

        return type.cast(proxy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getGlue() {
        return this.ros.getGlue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStrict() {
        return this.ros.isStrict();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDryRun() {
        return this.ros.isDryRun();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getFeaturePaths() {
        return this.ros.getFeaturePaths();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlugin(Object plugin) {
        this.ros.addPlugin(plugin);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> getFilters() {
        return this.ros.getFilters();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMonochrome() {
        return this.ros.isMonochrome();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SnippetType getSnippetType() {
        return this.ros.getSnippetType();
    }
}
