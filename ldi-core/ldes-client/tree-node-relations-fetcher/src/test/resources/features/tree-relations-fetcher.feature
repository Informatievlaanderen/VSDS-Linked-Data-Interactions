Feature: TreeRelationsFetcher
  As a user
  I want to use the TreeRelationsFetcher to retrieve a Tree Node Relation

  Scenario: Fetching an available TreeNode
    Given I have a TreeRelationsFetcher
    When I create a TreeNodeRequest with Lang "turtle" and url "http://localhost:10101/200-24-relations"
    And I fetch the TreeNodeRelations
    Then the obtained TreeNodeRelation has 24 relations

  Scenario: Fetching a redirect
    Given I have a TreeRelationsFetcher
    When I create a TreeNodeRequest with Lang "turtle" and url "http://localhost:10101/302-redirects"
    And I fetch the TreeNodeRelations
    Then the obtained TreeNodeRelation has 1 relations

  Scenario: Fetching a not-found Response
    Given I have a TreeRelationsFetcher
    When I create a TreeNodeRequest with Lang "turtle" and url "http://localhost:10101/404-not-found"
    Then An UnSupportedOperationException is thrown
