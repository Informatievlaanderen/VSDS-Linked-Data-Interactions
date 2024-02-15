Feature: StartingTreeNodeRelationsFinder

  Scenario: Determining all the starting TreeNodeRelations Starting from a View
    Given I have a StartingTreeNodeRelationsFinder
    And I have a StartingNodeRequest with a lang "nq" and url: "http://localhost:10101/200-treenode-is-also-view"
    When I execute the StartingNodeRequest
    Then the starting nodes contains following starting uris
      | https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z |

  Scenario: Determining the starting TreeNode Starting from a Tree Node that is not a view
    Given I have a StartingTreeNodeRelationsFinder
    And I have a StartingNodeRequest with a lang "turtle" and url: "http://localhost:10101/200-treenode-is-not-view"
    When I execute the StartingNodeRequest
    Then the starting nodes contains following starting uris
      | https://grar.smartdataspace.dev-vlaanderen.be/addresses/by-name?substring=le |

  Scenario: Determining the starting TreeNode Starting from an endpoint that redirects to a View
    Given I have a StartingTreeNodeRelationsFinder
    And I have a StartingNodeRequest with a lang "n-quads" and url: "http://localhost:10101/302-redirects-to-view"
    When I execute the StartingNodeRequest
    Then the starting nodes contains following starting uris
      | https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z |
