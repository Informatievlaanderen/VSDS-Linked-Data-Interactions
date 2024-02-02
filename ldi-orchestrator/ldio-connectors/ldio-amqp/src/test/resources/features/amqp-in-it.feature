Feature: AMQPInIntegrationTest
  As a user
  I want to use an AMQP input component to receive data from a AMQP broker

  Scenario Outline: Receiving a basic message from an amqp queue
    Given I create a queue for my scenario: <queue>
    And I prepare the result lists
    And I create default config for LdioJmsIn with <content-type>
    And I start a listener with an LdioJmsIn component
    And I send a model from <content> and <content-type> to broker using the amqp producer
    Then Wait for the message
    And The result value will contain the model
    And The componentExecutor will have been called

    Examples:
      | queue  | content-type        | content                                                                                                                                                                                                                                      |
      | nquads | application/n-quads | <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> 'my-zone-type' .                                                                                                                                                |
      | rdfxml | application/rdf+xml | <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:j.0="https://data.com/ns/mobiliteit#"><rdf:Description rdf:about="https://example.com/hindrances/29797"><j.0:zone>my-zone-type</j.0:zone></rdf:Description></rdf:RDF> |