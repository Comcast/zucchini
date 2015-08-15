package com.comcast.zucchini;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

    private final cucumber.runtime.Runtime runtime;
    private final StringBuilder output = new StringBuilder();
    private List<Formatter> formatters;

    /**
     * Bootstrap the cucumber runtime
     *
     * @param clazz Which has the cucumber.api.CucumberOptions and org.testng.annotations.Test annotations
     */
    public TestNGZucchiniRunner(Class clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        ResourceLoader resourceLoader = new MultiLoader(classLoader);

        RuntimeOptionsFactory runtimeOptionsFactory = new RuntimeOptionsFactory(clazz);
        RuntimeOptions runtimeOptions = runtimeOptionsFactory.create();

        /* Add the custom Zucchini Formatter */
        CucumberJSONFormatter formatter = new CucumberJSONFormatter(output);
        runtimeOptions.addPlugin(formatter);

        this.formatters = getAllPlugins(runtimeOptions);
//        this.formatters.add(runtimeOptions.formatter(classLoader));

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
        return output.toString();
    }
    
    private List<Formatter> getAllPlugins(RuntimeOptions runtimeOptions) {
        List<Formatter> rv = new ArrayList<Formatter>();
        try {
            Method plugins = RuntimeOptions.class.getDeclaredMethod("getPlugins", new Class[0]);
            plugins.setAccessible(true);
            List<Object> ps = (List<Object>) plugins.invoke(runtimeOptions, null);
            for (Object p : ps) {
                if (Formatter.class.isInstance(p)) {
                    rv.add((Formatter) p);
                }
            }
        }
        catch (Throwable e) {
            System.out.println("######## exception!!!\n" + e.getMessage());
            e.printStackTrace();
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
