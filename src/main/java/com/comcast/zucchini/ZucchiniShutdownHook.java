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
package com.comcast.zucchini;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;

import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Reportable;

class ZucchiniShutdownHook extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZucchiniShutdownHook.class);
    private static final String NAME_ENV_VAR = "ZUCCHINI_REPORT_NAME";
    public static final org.apache.logging.log4j.Logger NOOP = LogManager.getLogger(ReportBuilder.class); // Workaround: LogManager needs to be statically initialized outside of a shutdown hook
    private static ZucchiniShutdownHook instance = null;

    public static ZucchiniShutdownHook getDefault() {
        //double checked locking on the class instance to create singleton
        if(ZucchiniShutdownHook.instance == null) {
            synchronized(ZucchiniShutdownHook.class) {
                if(ZucchiniShutdownHook.instance == null) {
                    ZucchiniShutdownHook.instance = new ZucchiniShutdownHook();
                }
            }
        }

        //return singleton instance
        return ZucchiniShutdownHook.instance;
    }

    private List<String> zucchiniFailureCauses;

    private ZucchiniShutdownHook() {
        zucchiniFailureCauses = new LinkedList<String>();
    }

    @Override
    public void run() {
        LOGGER.trace("Running ZucchiniShutdownHook");
        FileWriter writer = null;
        try {
            if (0 == AbstractZucchiniTest.featureSet.size()) {
                LOGGER.warn("There are 0 features run");
            }

            for(Class<?> testClass : AbstractZucchiniTest.featureSet.keySet()) {
                ZucchiniOutput options = testClass.getAnnotation(ZucchiniOutput.class);
                String fileName = options != null ? options.json() : "target/zucchini.json";
                File html = options != null ? new File(options.html()) : new File("target/zucchini-reports");
  
                LOGGER.trace("Writing feature set out to {}", fileName);
                /* write the json first, needed for html generation */
                File json = new File(fileName);
                JsonArray features = AbstractZucchiniTest.featureSet.get(testClass);
                writer = new FileWriter(json);
                writer.write(features.toString());
                writer.close();
                writer = null;

                List<String> pathList = new LinkedList<String>();
                pathList.add(json.getAbsolutePath());

                String version = this.getClass().getPackage().getImplementationVersion();

                if(version == null) {
                    try {
                        InputStream ips = this.getClass().getClassLoader().getResourceAsStream("version.properties");

                        if(ips != null) {
                            Properties props = new Properties();
                            props.load(ips);

                            version = props.getProperty("version");
                        }
                    }
                    catch(IOException ioe) {
                        LOGGER.warn("{}", ioe);
                    }
                }

                if(version == null)
                    version = "";
                else
                    version = " - Zucchini (" + version + ")";

                String rptName = "${" + NAME_ENV_VAR + "}";

                if(System.getenv(NAME_ENV_VAR) != null)
                    rptName = System.getenv(NAME_ENV_VAR);


                Configuration reportConfig = new Configuration(html, "Zucchini" + version);
                reportConfig.setBuildNumber(rptName + version);
                ReportBuilder reportBuilder = new ReportBuilder(pathList, reportConfig);
                Reportable reportable = reportBuilder.generateReports();
                FileUtils.copyDirectory(new File(html, ReportBuilder.BASE_DIRECTORY), html); // move the cucumber base directory into zucchini
                FileUtils.deleteDirectory((new File(html, ReportBuilder.BASE_DIRECTORY)));

                boolean buildResult = (reportable != null) && reportable.getFailedSteps() == 0;
                if(!buildResult)
                    throw new Exception("BUILD FAILED - Check Report For Details");
            }
        }
        catch(Exception t) {
            LOGGER.error("FATAL ERROR: " + t.getMessage());
            Runtime.getRuntime().halt(-1);
        }
        finally {
            if(writer != null) {
                try {
                    writer.close();
                }
                catch(IOException ex) {
                    LOGGER.error("ERROR writing report:", ex);
                }
            }
        }

        if(this.zucchiniFailureCauses.size() > 0) {
            StringBuilder sb = new StringBuilder();

            int idx = 0;

            sb.append("Zucchini failed with the following errors:\n");
            for(String cause : this.zucchiniFailureCauses) {
                sb.append(String.format("Cause[%3d] :: %s\n", idx++, cause));
            }

            LOGGER.error(sb.toString());

            Runtime.getRuntime().halt(-1);
        }
    }

    public void addFailureCause(String cause) {
        synchronized(this.zucchiniFailureCauses) {
            this.zucchiniFailureCauses.add(cause);
        }
    }
}
