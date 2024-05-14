Feature: ChangeDetectionFilter
  As a data consumer
  I want to filter out state objects that had not changed

  Scenario Outline:
    Given A ChangeDetectionFilter with state persistence strategy <persistenceStrategy>
    When I receive member "members/state-member.nq"
    Then The filtered member is isomorphic with "members/state-member.ttl"
    When I receive member "members/state-member.nq"
    Then The filtered member is empty
    When I receive member "members/state-member.ttl"
    Then The filtered member is empty
    When I receive member "members/changed-state-member.nq"
    Then The filtered member is isomorphic with "members/changed-state-member.nq"
    Then The filter is destroyed

    Examples:
      | persistenceStrategy |
      | MEMORY              |
      | SQLITE              |
      | POSTGRES            |
