package com.comcast.csv.zucchini

import cucumber.api.CucumberOptions
import cucumber.api.testng.TestNgReporter
import cucumber.runtime.ClassFinder
import cucumber.runtime.CucumberException
import cucumber.runtime.RuntimeOptions
import cucumber.runtime.RuntimeOptionsFactory
import cucumber.runtime.formatter.CucumberJSONFormatter;
import cucumber.runtime.io.MultiLoader
import cucumber.runtime.io.ResourceLoader
import cucumber.runtime.io.ResourceLoaderClassFinder

/**
 * Glue code for running Cucumber via TestNG.
 */
class TestNGZucchiniRunner {
    
    private final cucumber.runtime.Runtime runtime;
    private final StringBuilder output = new StringBuilder()

    /**
     * Bootstrap the cucumber runtime
     *
     * @param clazz Which has the cucumber.api.CucumberOptions and org.testng.annotations.Test annotations
     */
    public TestNGZucchiniRunner(Class clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);

        Class[] annotationClasses = [CucumberOptions.class]
        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz, annotationClasses);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();

        CucumberJSONFormatter formatter = new CucumberJSONFormatter(output);
        runtimeOptions.addFormatter(formatter);
        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        runtime = new cucumber.runtime.Runtime(resourceLoader, classFinder, classLoader, runtimeOptions);
    }

    /**
     * Run the Cucumber features
     */
    public void runCukes() {
        try {
            runtime.run();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        if (!runtime.getErrors().isEmpty()) {
            throw new CucumberException(runtime.getErrors().get(0));
        } else if (runtime.exitStatus() != 0x00) {
            throw new CucumberException("There are pending or undefined steps.");
        }
    }
    
    public String getJSONOutput() {
        return output.toString()
    }
}
