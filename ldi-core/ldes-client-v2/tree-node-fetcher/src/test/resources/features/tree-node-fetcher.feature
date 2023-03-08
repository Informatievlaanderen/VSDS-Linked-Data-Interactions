Feature: TreeNodeFetcher
  As a user
  I want to use the TreeNodeFetcher to retrieve a Tree Node

  Scenario: Fetching an available TreeNode
    Given I have a TreeNodeFetcher
    When I create a TreeNodeRequest with Lang "jsonld" and url "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"
    And I fetch the TreeNode
    Then the obtained TreeNode has 250 members and 1 relations

#   TODO Test the other responses
#   TODO Test another content type