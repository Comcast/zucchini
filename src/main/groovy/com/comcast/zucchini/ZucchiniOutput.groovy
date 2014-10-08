/**
 * Copyright 2014 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.zucchini

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
    String json() default "target/zucchini.json";
    
    /**
     * The relative path from the execution directory where the HTML output folder should be generated.
     * 
     * @return where to write the final HTML output
     */
    String html() default "target/zucchini-reports";
}
