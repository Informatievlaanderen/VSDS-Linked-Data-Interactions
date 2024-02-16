Feature: AMQPOutIntegrationTest
  As a user
  I want to use an AMQP output component to send data to an AMQP broker

  Scenario Outline: Sending a basic message to an amqp queue
    Given I create a queue for my scenario: <queue>
    And I create a message consumer
    And I create a model with <content-type> and <content>
    And I create default config for LdioAmqpOut
    And I create an LdioAmqpOut component
    And I send the model to the LdioAmqpOut component
    Then The mock listener will wait for the message
    And The mock listener result will contain the content-type
    And The mock listener result will contain the model

    Examples:
      | queue  | content-type        | content                                                                                                                                                                                                                                      |
      | nquads | application/n-quads | <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> 'my-zone-type' .                                                                                                                                                |
      | rdfxml | application/rdf+xml | <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:j.0="https://data.com/ns/mobiliteit#"><rdf:Description rdf:about="https://example.com/hindrances/29797"><j.0:zone>my-zone-type</j.0:zone></rdf:Description></rdf:RDF> |