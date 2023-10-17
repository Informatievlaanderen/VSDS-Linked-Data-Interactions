Feature: HttpEnricher
  As a user
  I want to use a HttpEnricher to enrich the model using HTTP Requests

Scenario: Enriching the model with a GET request
  Given I have an RdfAdapter
  And I configure url property path "<http://example.org/url>"
  And I create an LdioHttpEnricher with the configured properties
  And I have a model with only an url property
  When I send the model to the enricher
  Then The result contains a model with both the input and the http response

#  Scenario Outline: Obtaining the Response of Request
#    Given I have a <requestExecutor>
#    When I create RequestHeaders
#    And I add a RequestHeader with key "Accept" and value "application/n-quads"
#    And I create a Request with the RequestHeaders and url: <endpoint>
#    And I execute the request
#    Then I obtain a response with status code 200
#
#    Examples:
#      | requestExecutor                  | endpoint                                          |
#      | DefaultRequestExecutor           | http://localhost:10101/200-response-accept-nquads |
#      | ClientCredentialsRequestExecutor | http://localhost:10101/200-response-with-token    |
#      | ApiKeyRequestExecutor            | http://localhost:10101/200-response-with-api-key  |
