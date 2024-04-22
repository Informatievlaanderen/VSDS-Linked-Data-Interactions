Feature: JsonToLDIntegrationTest
  As a user
  I want to use a adapter to convert json to json-ld

  Scenario: Transforming a basic json object
    Given I set a context in the configuration
    And I create the adapter
    When I send a json object
    Then The context is added

