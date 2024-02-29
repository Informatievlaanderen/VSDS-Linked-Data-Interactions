Feature: Management of pipelines in LDIO
  As a user
  I want to add and remove LDIO pipelines

  @creation
  Scenario Outline: Creation of pipelines
    When I start LDIO
    And I post a <file> <type> pipeline with a <statusCode> response
    And I expect <pipelineCount> pipelines
    Examples:
      | file      | type   | statusCode | pipelineCount |
      | "valid"   | "json" | 200        | 1             |
      | "valid"   | "yml"  | 200        | 1             |
      | "invalid" | "json" | 400        | 0             |
      | "invalid" | "yml"  | 400        | 0             |

  @creation @multiple
  Scenario Outline: Creation of pipelines
    When I start LDIO
    And I post a <firstfile> <type> pipeline with a <firstStatusCode> response
    And I post a <secondFile> <type> pipeline with a <secondStatusCode> response
    And I expect <pipelineCount> pipelines
    Examples:
      | firstfile | firstStatusCode | secondFile | secondStatusCode | type   | pipelineCount |
      | "valid"   | 200             | "valid"    | 400              | "json" | 1             |
      | "valid"   | 200             | "valid"    | 400              | "yml"  | 1             |
      | "valid"   | 200             | "valid-2"  | 200              | "json" | 2             |
      | "valid"   | 200             | "valid-2"  | 200              | "yml"  | 2             |
      | "valid"   | 200             | "invalid"  | 400              | "json" | 1             |
      | "valid"   | 200             | "invalid"  | 400              | "yml"  | 1             |
      | "invalid" | 400             | "invalid"  | 400              | "json" | 0             |
      | "invalid" | 400             | "invalid"  | 400              | "yml"  | 0             |

  @deletion
  Scenario: Deleting a pipeline
    When I start LDIO
    And I post a "valid" "yml" pipeline with a 200 response
    And I delete the "valid-pipeline" pipeline with a 200 response
    And I expect 0 pipelines

  @deletion
  Scenario: Deleting a non existent pipeline
    When I start LDIO
    And I delete the "valid-pipeline" pipeline with a 204 response
    And I expect 0 pipelines