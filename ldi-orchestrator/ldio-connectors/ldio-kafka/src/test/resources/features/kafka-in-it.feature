Feature: KafkaInIntegrationTest
  As a user
  I want to use a kafka input component to receive data from a kafka broker

#  Scenario Outline: Receiving a basic message from a kafka topic
#    Given I create a topic for my scenario: <topic>
#    And I prepare the result lists
#    And I create default config for LdioKafkaIn
#    And I start a listener with an LdioKafkaIn component
#    And I create a kafka producer
#    And I create a model from <content> and <content-type>
#    And I send the model to broker using the kafka producer
#    Then Wait for the message
#    And The result header will contain the <content-type>
#    And The result value will contain the model
#    And The componentExecutor will have been called
#
#    Examples:
#      | topic  | content-type        | content                                                                                                                                                                                                                                      |
#      | nquads | application/n-quads | <https://example.com/hindrances/29797> <https://data.com/ns/mobiliteit#zone> 'my-zone-type' .                                                                                                                                                |
#      | rdfxml | application/rdf+xml | <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:j.0="https://data.com/ns/mobiliteit#"><rdf:Description rdf:about="https://example.com/hindrances/29797"><j.0:zone>my-zone-type</j.0:zone></rdf:Description></rdf:RDF> |