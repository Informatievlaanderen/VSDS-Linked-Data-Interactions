Feature: TreeNodeFetcher
  As a user
  I want to use the TreeNodeFetcher to retrieve a Tree Node

  Scenario: Fetching an available TreeNode
    Given I have a TreeNodeFetcher
    When I create a TreeNodeRequest with Lang "jsonld" and url "http://localhost:10101/200-1-relation-3-members"
    And I fetch the TreeNode
    Then the obtained TreeNode has 3 members and 1 relations

#   TODO Test the other responses
#   TODO Test another content type