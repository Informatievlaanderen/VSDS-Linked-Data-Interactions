Feature: TreeNodeDiscoverer
  As an user
  I want to discover the TreeNodeRelations

  Scenario:
    Given I have a TreeDiscoverer
    When I start discovering the tree node relations
    Then I got a 5 relations