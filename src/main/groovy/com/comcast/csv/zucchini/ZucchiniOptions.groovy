package com.comcast.csv.zucchini

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * This annotation provides the subset of the options as the cucumber command line,
 * {@link cucumber.api.cli.Main}. This is required because the format
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
public @interface ZucchiniOptions {

    /**
     * @return the paths to the feature(s)
     */
    String[] features() default [];

    /**
     * @return where to look for glue code (stepdefs and hooks)
     */
    String[] glue() default [];

    /**
     * @return where to write the final output
     */
    String output();
}
