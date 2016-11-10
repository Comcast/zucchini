package com.comcast.zucchini;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberTagStatement;

/**
 * Iterator which allows for iterating through each scenario of the feature files.
 *
 * @author Trent Schmidt
 */
public class FastrunIterator implements Iterator<CucumberFeatureHolder> {
    Iterator<CucumberFeature> features;

    CucumberFeature currentFeature;
    Iterator<CucumberTagStatement> scenarios;

    final boolean sync;

    public FastrunIterator(Iterator<CucumberFeature> features, boolean sync) {
        this.features = features;
        this.sync = sync;
    }

    private boolean hasNextNoLock() {
        if (false == this.features.hasNext()) {
            if (null == this.scenarios) {
                return false;
            }
            return this.scenarios.hasNext();
        }

        return true;
    }

    private CucumberFeatureHolder nextNoLock() {
        CucumberFeatureHolder rv = null;

        if (null == this.scenarios) {
            this.currentFeature = this.features.next();
            List<CucumberTagStatement> l = this.currentFeature.getFeatureElements();

            if (l.size() < 1) {
                /* Obscure situation, where we have a feature with no scenarios */
                return new CucumberFeatureHolder(this.currentFeature, null);
            }
            this.scenarios = l.iterator();
        }

        try {
            CucumberTagStatement scenario = this.scenarios.next();
            rv = new CucumberFeatureHolder(currentFeature, scenario);
        } catch (NoSuchElementException e) {
            this.scenarios = null;
            /* We don't have any scenarios, so we should try the next feature */
            rv = this.nextNoLock();
        }

        return rv;
    }

    @Override
    public boolean hasNext() {
        boolean rv;
        if (this.sync) {
            synchronized (this) {
                rv = hasNextNoLock();
            }
        } else {
            rv = hasNextNoLock();
        }
        return rv;
    }

    @Override
    public CucumberFeatureHolder next() {
        CucumberFeatureHolder rv = null;
        if (this.sync) {
            synchronized (this) {
                rv = this.nextNoLock();
            }
        } else {
            rv = this.nextNoLock();
        }
        return rv;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove() not supported for Fastrun");
    }
}
