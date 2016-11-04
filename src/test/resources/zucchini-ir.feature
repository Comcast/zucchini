@SMOKE-TEST @MULTI-TEST @FASTRUN-TEST
Feature: Zucchini must be able to run multiple tests in the same folder

    Scenario: Verify that this scenario can also run
        Given The vegetable exists
        And There is more than one
        Then You have at least two vegetables.
