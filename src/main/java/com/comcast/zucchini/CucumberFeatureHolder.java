package com.comcast.zucchini;

import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberTagStatement;

/**
 * Allows for statement to feature linkage of Cucumber classes
 *
 * @author Trent Schmidt
 */
public class CucumberFeatureHolder  {
    final CucumberFeature feature;
    final CucumberTagStatement statement;
    
    public CucumberFeatureHolder(CucumberFeature feature, CucumberTagStatement statement) {
        this.feature = feature;
        this.statement = statement;
    }
    
    public CucumberFeature getFeature() {
        return this.feature;
    }
    
    public CucumberTagStatement getStatement() {
        return this.statement;
    }
}