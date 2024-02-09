Feature: LdesClientIntegrationTest
  As a user
  I want to be able to use an LDES Client as input for LDIO

  Scenario: Reading an LDES Stream
    Given I start an ldes-ldio-in component with url "/exampleData?generatedAtTime=2022-05-03T00:00:00.000Z"
    Then All 6 members from the stream are passed to the pipeline