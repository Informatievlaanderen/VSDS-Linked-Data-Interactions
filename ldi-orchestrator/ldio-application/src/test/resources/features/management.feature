Feature: Management of pipelines in LDIO
  As a user
  I want to add and remove LDIO pipelines

  @creation
  Scenario: Creating a pipeline with a valid json
    When I start LDIO
    And I post a valid json pipeline
    Then I expect a 200 response
    And I expect 1 pipelines

  @creation
  Scenario: Creating a pipeline with a valid yaml
    When I start LDIO
    And I post a valid yaml pipeline
    Then I expect a 200 response
    And I expect 1 pipelines

  @creation
  Scenario: Creating a pipeline with an invalid json
    When I start LDIO
    And I post an invalid json pipeline
    Then I expect a 400 response
    And I expect 0 pipelines

  @creation
  Scenario: Creating a pipeline with an invalid yaml
    When I start LDIO
    And I post an invalid yaml pipeline
    Then I expect a 400 response
    And I expect 0 pipelines

  @deletion
  Scenario: Deleting a pipeline
    When I start LDIO
    And I post a valid yaml pipeline
    And I delete the "valid-pipeline" pipeline
    Then I expect a 200 response
    And I expect 0 pipelines

  @deletion
  Scenario: Deleting a non existent pipeline
    When I start LDIO
    And I delete the "valid-pipeline" pipeline
    Then I expect a 404 response
    And I expect 0 pipelines