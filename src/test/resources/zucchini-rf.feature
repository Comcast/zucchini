@FASTRUN-TEST
Feature: Zucchini Must Share the Scenarios across contexts

    Scenario: Exciting scenario 1
        Given The scenario 1 is saved to global list
        And We take a 2500 ms nap
        Then Verify no scenario duplicates

    Scenario: Exciting scenario 2
        Given The scenario 2 is saved to global list
        And We take a 2500 ms nap
        Then Verify no scenario duplicates

    Scenario: Exciting scenario 3
        Given The scenario 3 is saved to global list
        And We take a 2500 ms nap
        Then Verify no scenario duplicates

    Scenario Outline: Exciting scenario example
        Given The scenario <num> is saved to global list
        And We take a 500 ms nap
        Then Verify no scenario duplicates

        Examples:
            | num |
            |  4  |
            |  5  |
