package com.comcast.zucchini;

import java.util.List;
import java.util.HashMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import cucumber.api.SnippetType;
import cucumber.api.StepDefinitionReporter;
import cucumber.api.SummaryPrinter;

import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.Utils;
import cucumber.runtime.Env;
import cucumber.runtime.formatter.PluginFactory;
import cucumber.runtime.model.CucumberFeature;

import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;

public class ZucchiniRuntimeOptions extends RuntimeOptions {
    private RuntimeOptions ros;

    public ZucchiniRuntimeOptions(String argv) {
        super(argv);
        this.ros = this;
    }

    public ZucchiniRuntimeOptions(List<String> argv) {
        super(argv);
        this.ros = this;
    }

    public ZucchiniRuntimeOptions(Env env, List<String> argv) {
        super(env, argv);
        this.ros = this;
    }

    public ZucchiniRuntimeOptions(PluginFactory pluginFactory, List<String> argv) {
        super(pluginFactory, argv);
        this.ros = this;
    }

    public ZucchiniRuntimeOptions(Env env, PluginFactory pluginFactory, List<String> argv) {
        super(env, pluginFactory, argv);
        this.ros = this;
    }

    public ZucchiniRuntimeOptions(RuntimeOptions ros) {
        super(""); //wasted option
        this.ros = ros;
    }

    public List<CucumberFeature> cucumberFeatures(ResourceLoader resourceLoader) {
        return this.ros.cucumberFeatures(resourceLoader);
    }

    public Formatter formatter(ClassLoader classLoader) {
        return pluginProxy(classLoader, Formatter.class);
    }

    public Reporter reporter(ClassLoader classLoader) {
        return pluginProxy(classLoader, Reporter.class);
    }

    public StepDefinitionReporter stepDefinitionReporter(ClassLoader classLoader) {
        return pluginProxy(classLoader, StepDefinitionReporter.class);
    }

    public SummaryPrinter summaryPrinter(ClassLoader classLoader) {
        return pluginProxy(classLoader, SummaryPrinter.class);
    }

    @Override
    public <T> T pluginProxy(ClassLoader classLoader, final Class<T> type) {
        Object baseProxy = this.ros.pluginProxy(classLoader, type);

        Object proxy = Proxy.newProxyInstance(classLoader, new Class<?>[]{type}, new ZucchiniInvocationHandler(baseProxy, type));

        return type.cast(proxy);
    }

    public List<String> getGlue() {
        return this.ros.getGlue();
    }

    public boolean isStrict() {
        return this.ros.isStrict();
    }

    public boolean isDryRun() {
        return this.ros.isDryRun();
    }

    public List<String> getFeaturePaths() {
        return this.ros.getFeaturePaths();
    }

    public void addPlugin(Object plugin) {
        this.ros.addPlugin(plugin);
    }

    public List<Object> getFilters() {
        return this.ros.getFilters();
    }

    public boolean isMonochrome() {
        return this.ros.isMonochrome();
    }

    public SnippetType getSnippetType() {
        return this.ros.getSnippetType();
    }
}
