Feature: Starting up of LDIO
  As a user
  I want to start LDIO with defined pipelines configs

  @file-based
  Scenario: Starting with a valid file-based pipeline
    When I start LDIO with the "valid-file" profile
    Then I expect 1 pipelines

  @file-based
  Scenario: Starting with invalid file-based pipeline
    Given I start LDIO with the "invalid-file" profile
    Then I expect 0 pipelines

  @config-based
  Scenario: Starting with a valid config-based pipeline
    Given I start LDIO with the "valid-config" profile
    Then I expect 1 pipelines

  @config-based
  Scenario: Starting with invalid config-based pipeline
    When I start LDIO with the "invalid-config" profile
    Then I expect 0 pipelines

  @combined
  Scenario: Starting with a valid file-based and config-based pipeline with same name
    When I start LDIO with the "valid-file-and-config" profile
    Then I expect 1 pipelines
    And The expected pipeline has 1 transformers