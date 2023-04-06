Feature: KafkaOutIntegrationTest
  As a user
  I want to use a RequestExecutor to execute HTTP Requests

  Scenario: Obtaining the Response of Request
    Given I have content-type "application/n-quads"
    And topic "quickstart-events"
    When I run
    Then I woo