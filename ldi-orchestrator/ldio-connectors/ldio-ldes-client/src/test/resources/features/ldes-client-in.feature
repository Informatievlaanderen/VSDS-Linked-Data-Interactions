Feature: LdesClientIntegrationTest
  As a user
  I want to be able to use an LDES Client as input for LDIO

  Scenario: Reading an LDES Stream
    Given I want to follow the following LDES
      | /exampleData?generatedAtTime=2022-05-03T00:00:00.000Z |
    When I start an ldes-ldio-in component
    Then All 6 members from the stream are passed to the pipeline

#  Scenario: Reading multiple LDES Streams
#    Given I want to follow the following LDES
#      | /items/grouped?group=1 |
#      | /items/grouped?group=2 |
#    And I configure this to be of RDF format "text/turtle"
#    When I start an ldes-ldio-in component
#    Then All 8 members from the stream are passed to the pipeline