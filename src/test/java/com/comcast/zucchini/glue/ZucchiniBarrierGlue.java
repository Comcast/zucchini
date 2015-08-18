package com.comcast.zucchini.glue;

import org.testng.Assert;
import static org.testng.Assert.assertEquals;

import com.comcast.zucchini.BarrierTest;
import com.comcast.zucchini.Barrier;
import com.comcast.zucchini.TestContext;
import com.comcast.zucchini.Veggie;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZucchiniBarrierGlue {

    private static int barrierCount = 0;

    private static Logger logger = LoggerFactory.getLogger(ZucchiniBarrierGlue.class);

    public static String name() {
        return TestContext.getCurrent().name();
    }

    @Given("We have a blank method here")
    public void blank() {}

    @And("We sync fast here")
    public void fast() throws Throwable {
        synchronized(logger) {
            logger.debug("Entering barrier on " + name());
        }
        Barrier.sync();
        synchronized(logger) {
            logger.debug("Leaving barrier on "  + name());
        }
        Barrier.sync();
    }

    @Then("We should be just fine")
    public void fine() {}

    @And("We sync slow here")
    public void slow() throws Throwable {
        logger.debug("Entering barrier on " + name());
        if(name().equals("ThreadIdx[0]")) {
            try {
                Thread.sleep(50);
            }
            catch(Throwable t) {
                logger.debug("The sleep was broken.");
            }
        }

        Barrier.sync(150);
    }

    @Then("Our sync times out and proceeds")
    public void timeout() {}

    @And("We timeout and catch the timeout here")
    public void fail_and_catch_here() throws Throwable {
        if(name().equals("ThreadIdx[0]")) {
            try {
                Barrier.sync(50); //wait half a second
            }
            catch(Throwable t) {
                synchronized(logger) {
                    logger.error("Sync still passed.", t);
                }
                throw t;
            }
        }
        else {
            try {
                Thread.sleep(100);
            }
            catch(ThreadDeath ex) {
                //ignore, this is proper behavior
                return;
            }
            catch(Throwable t) {
                synchronized(logger) {
                    logger.error("Thread failed to stop.", t);
                }
                throw t;
            }
        }
    }

    @Then("The last step properly failed")
    public void empty_step() throws Throwable {
    }

    @Given("We are testing thread abort")
    public void abort_test_start() throws Throwable {
        Barrier.sync(); //sync on startup to ensure that previous time-sensitive syncs don't false fail
    }

    @And("We have one thread fail")
    public void one_thread_fail() throws Throwable {
        if(name().equals("ThreadIdx[0]")) {
            Assert.fail("THIS IS SUPPOSED TO FAIL.");
        }
        else {
            Thread.sleep(100);
        }

        Barrier.sync(200);
    }

    @Then("Our barrier still runs")
    public void barrier_still_works() throws Throwable {
        Barrier.sync(100);
    }

    @Given("We have one barrier")
    public void first_barrier() throws Throwable {
        synchronized(this) {
            barrierCount++;
        }
        Barrier.sync();
    }

    @And("We have another barrier")
    public void second_barrier() throws Throwable {
        logger.debug(String.format("BarrierCount for [%s] is %d, and numContexts is %d after sync.\n", name(),  barrierCount, BarrierTest.numContexts));
        if(barrierCount != BarrierTest.numContexts) {
            Assert.fail(String.format("The internal barrierCount[%d] does not match the number of contexts[%d]", barrierCount, BarrierTest.numContexts));
        }
        Barrier.sync();
        synchronized(this) {
            barrierCount--;
        }
    }

    @Then("Our barriers executed in order")
    public void barrier_serial_end() {
        Barrier.sync();
        if(barrierCount != 0) {
            Assert.fail(String.format("The internal barrierCount[%d] was not set to zero.", barrierCount));
        }
    }
}
