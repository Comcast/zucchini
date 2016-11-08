@FASTRUN-TEST
Feature: Zucchini Must Share the Same Vegetables for all Tests

    Scenario: Verify the vegetable is delicious when cleaned and cooked
        Given The vegetable is clean
        And The vegetable is cooked
        And We wait 5 seconds for it to cool
        Then The vegetable tastes delicious

    Scenario: Verify the vegetable is gross when not cleaned and cooked
        Given The vegetable is dirty
        And The vegetable is cooked
        And We wait 2 seconds for it to cool
        Then The vegetable tastes gross

    Scenario: Verify the vegetable is crunchy when cleaned but raw
        Given The vegetable is clean
        And The vegetable is raw
        And We wait 2 seconds for it to cool
        Then The vegetable tastes crunchy
