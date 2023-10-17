Feature: HttpEnricher
  As a user
  I want to use a HttpEnricher to enrich the model using HTTP Requests

Scenario: Foo
  Given I have foo

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
