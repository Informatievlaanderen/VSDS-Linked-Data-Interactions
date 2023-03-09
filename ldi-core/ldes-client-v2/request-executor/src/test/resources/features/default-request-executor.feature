Feature: DefaultRequestExecutor
  As a user
  I want to use the DefaultRequestExecutor to execute HTTP Requests

  Scenario: Obtaining the Response of a simple Request
    Given I have a RequestExecutor
    When I create RequestHeaders
    And I add a RequestHeader with key "Accept" and value "application/ld+json"
    And I create a Request with the RequestHeaders and url: "http://localhost:10101/200-response-accept-json-ld"
    And I execute the request
    Then I obtain a response with status code 200

  Scenario: Obtaining the Response of a redirected Request
    Given I have a RequestExecutor
    When I create RequestHeaders
    And I add a RequestHeader with key "Accept" and value "application/ld+json"
    And I create a Request with the RequestHeaders and url: "http://localhost:10101/302-response-accept-json-ld"
    And I execute the request
    Then I obtain a response with status code 302
    And I obtain a location header: "http://localhost:10101/redirect-location"

  Scenario: Obtaining the Response of an Already Cached Response
    Given I have a RequestExecutor
    When I create RequestHeaders
    And I add a RequestHeader with key "Accept" and value "text/turtle"
    And I add a RequestHeader with key "If-None-Match" and value "1e0d1c-54e36ac89d1c0"
    And I create a Request with the RequestHeaders and url: "http://localhost:10101/304-response"
    And I execute the request
    Then I obtain a response with status code 304

  Scenario: Obtaining a HttpRequestException of an invalid Request
    Given I have a RequestExecutor
    When I create RequestHeaders
    And I create a Request with the RequestHeaders and url: "https://not-real"
    Then I get a HttpRequestException when executing the request