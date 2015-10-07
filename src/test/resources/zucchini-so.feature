@OUTLINE-BARRIER
Feature: Zucchini must be able to keep using Scenario Outlines with the Barrier

    Scenario Outline: Verify that scenario outlines work with barriers
        Given we set the name to <name>
        And the first context to finish fails
        Then the test terminates
        Examples:
            | name |
            | bob |
            | alice |
            | sam |
