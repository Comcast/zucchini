@SMOKE-TEST @BARRIER-FAILURE
Feature: Zucchini must be able to continue after all contexts fail on a scenario
    Background:
        Given we barrier in the init

    Scenario:
        Given we run this step
        Then we fail here

    Scenario:
        Given we run this step
        Then we don't fail
