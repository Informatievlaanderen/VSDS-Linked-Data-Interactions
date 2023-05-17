Feature: RequestExecutor
  As a user
  I want to use a RequestExecutor to execute HTTP Requests

  Scenario Outline: Obtaining the Response of Request
    Given I have a <requestExecutor>
    When I create RequestHeaders
    And I add a RequestHeader with key "Accept" and value "application/n-quads"
    And I create a Request with the RequestHeaders and url: <endpoint>
    And I execute the request
    Then I obtain a response with status code 200

    Examples:
      | requestExecutor                  | endpoint                                          |
      | DefaultRequestExecutor           | http://localhost:10101/200-response-accept-nquads |
      | ClientCredentialsRequestExecutor | http://localhost:10101/200-response-with-token    |
      | ApiKeyRequestExecutor            | http://localhost:10101/200-response-with-api-key  |

  Scenario Outline: Obtaining the Response of a redirected Request
    Given I have a <requestExecutor>
    When I create RequestHeaders
    And I add a RequestHeader with key "Accept" and value "application/n-quads"
    And I create a Request with the RequestHeaders and url: http://localhost:10101/302-response-accept-nquads
    And I execute the request
    Then I obtain a response with status code 302
    And I obtain a location header: "http://localhost:10101/redirect-location"

    Examples:
      | requestExecutor                  |
      | DefaultRequestExecutor           |
      | ClientCredentialsRequestExecutor |

  Scenario Outline: Obtaining the Response of an Already Cached Response
    Given I have a <requestExecutor>
    When I create RequestHeaders
    And I add a RequestHeader with key "Accept" and value "text/turtle"
    And I add a RequestHeader with key "If-None-Match" and value "1e0d1c-54e36ac89d1c0"
    And I create a Request with the RequestHeaders and url: http://localhost:10101/304-response
    And I execute the request
    Then I obtain a response with status code 304

    Examples:
      | requestExecutor                  |
      | DefaultRequestExecutor           |
      | ClientCredentialsRequestExecutor |

  Scenario Outline: Obtaining a HttpRequestException of an invalid Request
    Given I have a <requestExecutor>
    When I create a Request with the RequestHeaders and url: https://not-real
    Then I get a HttpRequestException when executing the request

    Examples:
      | requestExecutor                  |
      | DefaultRequestExecutor           |
      | ClientCredentialsRequestExecutor |

  Scenario: Obtaining a cached token when sending multiple authenticated requests
    Given I have a ClientCredentialsRequestExecutor
    When I create a Request with the RequestHeaders and url: http://localhost:10101/200-response-with-token
    And I execute the request
    And I execute the request
    Then I will have called the token endpoint only once

  Scenario: Obtaining the response with retries
    Given I have a requestExecutor which does 3 retries
    When I create a Request with the RequestHeaders and url: http://localhost:10101/500-response
    And I execute the request
    Then I will have called "/500-response" 3 times
    And I obtain a response with status code 500
    When I create a Request with the RequestHeaders and url: http://localhost:10101/fail-once
    And I mock "/fail-once" to fail the first time and succeed the second time
    And I execute the request
    Then I will have called "/fail-once" 2 times
    And I obtain a response with status code 200

  Scenario: Obtaining the response with retries after adding custom retry status codes
    Given I have a requestExecutor which does 3 retries with custom http status code 418
    When I create a Request with the RequestHeaders and url: http://localhost:10101/418-response
    And I execute the request
    Then I will have called "/418-response" 3 times
    And I obtain a response with status code 418