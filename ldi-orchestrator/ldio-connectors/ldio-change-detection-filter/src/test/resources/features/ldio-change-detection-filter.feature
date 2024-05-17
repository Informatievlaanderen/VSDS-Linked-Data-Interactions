Feature: LdioChangeDetectionFilter
  As a data consumer using LDIO
  I can filter out state members that have already been processed

  Scenario Outline:
    Given An LdioChangeDetectionFilter with a "<state>" persistence strategy
    When I send model from "members/state-member.nq" to the LdioChangeDetectionFilter
    Then The result contains 1 model
    When I send model from "members/state-member.ttl" to the LdioChangeDetectionFilter
    Then The result contains 1 model
    When I send model from "members/changed-state-member.nq" to the LdioChangeDetectionFilter
    Then The result contains 2 model
    Then I shutdown the LdioChangeDetectionFilter
    Examples:
      | state    |
      | MEMORY   |
      | SQLITE   |
      | POSTGRES |
