Feature: TreeRelationsDiscoverer
  As an user
  I want to discover the TreeRelations

  Scenario:
    Given I have a LdesStructureDiscoverer
    When I start discovering the tree node relations
    Then I got 4 child relations
    And I got a 6 relations in total