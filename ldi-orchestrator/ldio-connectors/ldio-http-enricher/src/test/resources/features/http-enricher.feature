Feature: HttpEnricher
  As a user
  I want to use a HttpEnricher to enrich the model using HTTP Requests

Scenario: Enriching the model with a GET request
  Given I have an RdfAdapter
  And I configure url property path "<http://example.org/url>"
  And I create an LdioHttpEnricher with the configured properties
  And I have a model with only an url property
  When I send the model to the enricher
  Then The result contains 1 model
  And The result contains a model with both the input and the http response

  Scenario: Enriching the model with a POST request
    Given I have an RdfAdapter
    And I configure url property path "<http://example.org/url>"
    And I configure body property path "<http://example.org/meta>/<http://example.org/body>"
    And I configure header property path "<http://example.org/meta>/<http://example.org/headers>"
    And I configure httpMethod property path "<http://example.org/meta>/<http://example.org/method>"
    And I create an LdioHttpEnricher with the configured properties
    And I have a model with everything
    When I send the model to the enricher
    Then The result contains 1 model
    And The result contains a model with both the input and the http response

  Scenario: Enriching the model with an invalid HTTP method
    Given I have an RdfAdapter
    And I configure url property path "<http://example.org/url>"
    And I configure httpMethod property path "<http://example.org/method>"
    And I create an LdioHttpEnricher with the configured properties
    And I have a model with an invalid http method
    Then The enricher should throw an exception when I send the model to the enricher
