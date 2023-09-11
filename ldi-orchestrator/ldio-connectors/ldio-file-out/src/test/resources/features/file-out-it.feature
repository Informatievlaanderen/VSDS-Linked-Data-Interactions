Feature: FileOutIntegrationTest
  As a user
  I want to use a file output component to send models to a file structure

  Scenario: Writing a model to a file
    Given I an empty archive-dir "target/archive"
    And I create a file-out-component with the archive-dir and timestampPath "http://www.w3.org/ns/prov#generatedAtTime"
    And I have a model defined in "model-with-timestamp20220520.nq" containing this timestampPath
    When I send the model to the file-out-component
    Then The model is written to "target/archive/2022/05/20/2022-05-20-09-58-15-867000000.ttl"
    When I have another model defined in "other-model-with-timestamp20220520.nq"
    And I send the model to the file-out-component
    Then The other model is written to "target/archive/2022/05/20/2022-05-20-09-58-15-867000000-1.ttl"
    When I have another model defined in "third-model-with-timestamp20220520.nq"
    And I send the model to the file-out-component
    Then The other model is written to "target/archive/2022/05/20/2022-05-20-09-58-15-867000000-2.ttl"

