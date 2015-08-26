package com.comcast.zucchini;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.runtime.ClassFinder;
import cucumber.runtime.CucumberException;
import cucumber.runtime.RuntimeOptions;
import cucumber.runtime.RuntimeOptionsFactory;
import cucumber.runtime.formatter.CucumberJSONFormatter;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import gherkin.formatter.Formatter;

/**
 * Glue code for running Cucumber via TestNG.
 */
public class TestNGZucchiniRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestNGZucchiniRunner.class);

    private final cucumber.runtime.Runtime runtime;
    private final StringBuilder output = new StringBuilder();
    private List<Formatter> formatters;

    /**
     * Bootstrap the cucumber runtime
     *
     * @param clazz Which has the cucumber.api.CucumberOptions and org.testng.annotations.Test annotations
     */
    public TestNGZucchiniRunner(Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);

        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();

        /* Add the custom Zucchini Formatter */
        CucumberJSONFormatter formatter = new CucumberJSONFormatter(output);
        runtimeOptions.addPlugin(formatter);

        this.formatters = getAllPlugins(runtimeOptions);

        ClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, classLoader);
        runtime = new ZucchiniRuntime(resourceLoader, classFinder, classLoader, runtimeOptions);
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
        return output.toString();
    }

    /**
     * Gets all plugins by calling {@link RuntimeOptions#getPlugins()} method using reflection.
     * This is needed in order to add custom configuration to already instantiated formatters / reporters.
     * This method is called after all Formatter / Reporter classes have been instantiated
     *
     * @param runtimeOptions Reference to the Cucumber's RuntimeOptions
     * @return List of Formatter objects
     */
    private List<Formatter> getAllPlugins(RuntimeOptions runtimeOptions) {
        List<Formatter> rv = new ArrayList<Formatter>();
        try {
            // find a method in RuntimeOptions class called 'getPlugins' that takes no arguments
            Method plugins = RuntimeOptions.class.getDeclaredMethod("getPlugins", new Class[0]);
            plugins.setAccessible(true);

            List<Object> ps = (List<Object>) plugins.invoke(runtimeOptions, null);
            for (Object p : ps) {
                // filter out only instances of type Formatter
                if (Formatter.class.isInstance(p)) {
                    rv.add((Formatter) p);
                }
            }
        }
        catch (Throwable e) {
            LOGGER.error("There was an exception while trying to call 'getPlugins' method", e);
        }

        return rv;
    }

    /**
     * Returns list of formatters used by Cucumber
     * @return List of {@link Formatter}
     */
    public List<Formatter> getFormatters() {
        return this.formatters;
    }
}
