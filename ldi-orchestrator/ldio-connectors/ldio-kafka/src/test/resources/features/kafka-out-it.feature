Feature: KafkaOutIntegrationTest
  As a user
  I want to use a kafka output component to send models to a kafka broker

  Scenario: Sending a basic message to a kafka topic
    Given I create a topic for my scenario: basic-message
    And I create default config for LdioKafkaOut
    And I create an LdioKafkaOut component
    And I start a kafka listener
    And I create a model
    And I send the model to the LdioKafkaOut component
    Then The listener will wait for the message
    And The mock listener result header will contain the content-type
    And The mock listener result value will contain the model

  Scenario: Sending a message with a kafka key property
    Given I create a topic for my scenario: message-with-key
    And I create default config for LdioKafkaOut
    And I configure a key property path "<https://data.com/ns/mobiliteit#zone>"
    And I create an LdioKafkaOut component
    And I start a kafka listener
    And I create a model
    And I add an n-quad to the model: "<https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> 'my-zone-type' ."
    And I send the model to the LdioKafkaOut component
    Then The listener will wait for the message
    And The mock listener result header will contain the content-type
    And The mock listener result value will contain the model
    And The result key will be "my-zone-type"
