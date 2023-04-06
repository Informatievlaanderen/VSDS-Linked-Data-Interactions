Feature: KafkaOutIntegrationTest
  As a user
  I want to use a kafka output component to send models to a kafka broker

  Scenario: Sending a basic message to a kafka topic
    Given I start a kafka broker with topic "my-topic"
    And I create an LdioKafkaOut component for topic "my-topic"
    And I start a kafka listener for topic "my-topic"
    And I create a model
    And I send the model to the LdioKafkaOut component
    Then The listener will wait for the message
    And The result header will contain the content-type
    And The result value will contain the model