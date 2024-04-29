Feature: LdesClientIntegrationTest
  As a user
  I want to be able to use an LDES Client as input for LDIO

  Scenario: Reading an LDES Stream
    Given I want to follow the following LDES
      | /exampleData?generatedAtTime=2022-05-03T00:00:00.000Z |
    And I configure this to be of RDF format "application/ld+json"
    When I start an ldes-ldio-in component
    Then All 6 members from the stream are passed to the pipeline

  Scenario: Reading multiple LDES Streams
    Given I want to follow the following LDES
      | /items/grouped?group=1 |
      | /items/grouped?group=2 |
    When I start an ldes-ldio-in component
    Then All 8 members from the stream are passed to the pipeline

  Scenario Outline: Reading an LDES Stream with latest state filter enabled
    Given I want to follow the following LDES
      | /exampleData?generatedAtTime=2022-05-03T00:00:00.000Z |
    And I want to add the following properties
      | materialisation.enabled             | true                                      |
      | materialisation.enable-latest-state | <isLatestStateFilterEnabled>              |
      | materialisation.version-of-property | http://purl.org/dc/terms/isVersionOf      |
      | timestamp-path                      | http://www.w3.org/ns/prov#generatedAtTime |
    And I configure this to be of RDF format "application/ld+json"
    When I start an ldes-ldio-in component
    Then All <expectedMemberSize> members from the stream are passed to the pipeline

    Examples:
      | isLatestStateFilterEnabled | expectedMemberSize |
      | true                       | 3                  |
      | false                      | 6                  |


  Scenario Outline: Reading an LDES Stream with exactly once filter enabled
    Given I want to follow the following LDES
      | /items/grouped?group=1 |
      | /items/grouped?group=3 |
    And I want to add the following properties
      | enable-exactly-once | <isExactlyOnceFilterEnabled> |
    When I start an ldes-ldio-in component
    Then All <expectedMemberSize> members from the stream are passed to the pipeline

    Examples:
      | isExactlyOnceFilterEnabled | expectedMemberSize |
      | true                       | 4                  |
      | false                      | 8                  |
