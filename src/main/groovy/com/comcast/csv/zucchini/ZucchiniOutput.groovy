package com.comcast.csv.zucchini

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Configure the JSON file output location of Zucchini.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE])
public @interface ZucchiniOutput {

    /**
     * The relative path from the execution directory where the combined JSON file should be saved.
     * 
     * @return where to write the final output
     */
    String value();
}
