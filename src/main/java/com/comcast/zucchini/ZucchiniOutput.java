package com.comcast.zucchini;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configure the JSON file output location of Zucchini.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ZucchiniOutput {

    /**
     * The relative path from the execution directory where the combined JSON file should be saved.
     *
     * @return where to write the final output
     */
    String json() default "target/zucchini.json";

    /**
     * The relative path from the execution directory where the HTML output folder should be generated.
     *
     * @return where to write the final HTML output
     */
    String html() default "target/zucchini-reports";
}
