Feature: DefaultRequestExecutor
  As a user
  I want to use the DefaultRequestExecutor to execute HTTP Requests

  Scenario: Obtaining the Response of a simple Request
    Given I have a RequestExecutor
    When I create RequestHeaders
    And I add a RequestHeader with key "Accept" and value "application/ld+json"
    And I create a Request with the RequestHeaders and url: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"
    And I execute the request
    Then I obtain a response with status code 200

  Scenario: Obtaining the Response of a redirected Request
    Given I have a RequestExecutor
    When I create RequestHeaders
    And I add a RequestHeader with key "Accept" and value "application/ld+json"
    And I create a Request with the RequestHeaders and url: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances"
    And I execute the request
    Then I obtain a response with status code 302

  Scenario: Obtaining the Response of an Already Cached Response
    Given I have a RequestExecutor
    When I create RequestHeaders
    And I add a RequestHeader with key "Accept" and value "text/turtle"
    And I create a Request with the RequestHeaders and url: "https://grar.smartdataspace.dev-vlaanderen.be/addresses/by-name?substring=a"
    And I execute the request
    Then I obtain a response with status code 200
    And I extract the etag from the response
    And I create RequestHeaders
    And I add a RequestHeader with key "Accept" and value "text/turtle"
    And I add a RequestHeader with key "If-None-Match" and value the obtained etag
    And I create a Request with the RequestHeaders and url: "https://grar.smartdataspace.dev-vlaanderen.be/addresses/by-name?substring=a"
    And I execute the request
    Then I obtain a response with status code 304