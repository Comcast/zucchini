@SMOKE-TEST @BARRIER
Feature: Zucchini must be able to use cross-context barrier syncs

    Scenario: Verify that the barrier sync triggers in the right order
        Given We have a blank method here
        And We sync fast here
        Then We should be just fine

    Scenario: Verify that the timeout exception is triggered for barrier
        Given We have a blank method here
        And We sync slow here
        Then Our sync times out and proceeds

    Scenario: Verify that a timeout will cause other threads to fail.
        Given We have a blank method here
        And We timeout and catch the timeout here
        Then The last step properly failed

    Scenario: Verify that barriers enforce serial execution before failure
        Given We have one barrier
        And We have another barrier
        Then Our barriers executed in order

    Scenario: We are testing that the barrier still works after one thread has failed
        Given We are testing thread abort
        And We have one thread fail
        Then Our barrier still runs

    Scenario: Verify that barriers enforce serial execution after failure
        Given We have one barrier
        And We have another barrier
        Then Our barriers executed in order
