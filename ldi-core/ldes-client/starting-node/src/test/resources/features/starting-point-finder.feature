Feature: StartingTreeNodeFinder
  As a user
  I want to use the StartingTreeNodeFinder to determine the starting Tree Node of an LDES Stream

  Scenario: Determining the starting TreeNode Starting from a View
    Given I have a StartingTreeNodeFinder
    When I create a StartingNodeRequest with a lang "jsonld" and url: "http://localhost:10101/200-treenode-is-also-view"
    Then the starting Tree Node of the LDES Stream is the url of the View: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"

  Scenario: Determining the starting TreeNode Starting from an endpoint that redirects to a View
    Given I have a StartingTreeNodeFinder
    When I create a StartingNodeRequest with a lang "jsonld" and url: "http://localhost:10101/302-redirects-to-view"
    Then the starting Tree Node of the LDES Stream is the url of the View: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"

  Scenario: Determining the starting TreeNode Starting from a Tree Node that is not a view
    Given I have a StartingTreeNodeFinder
    When I create a StartingNodeRequest with a lang "turtle" and url: "http://localhost:10101/200-treenode-is-not-view"
    Then the starting Tree Node of the LDES Stream is the url of the View: "https://grar.smartdataspace.dev-vlaanderen.be/addresses/by-name?substring=le"

  Scenario: Determining the starting TreeNode Starting from an endpoint that redirects to an infinite loop
    Given I have a StartingTreeNodeFinder
    When I create a StartingNodeRequest with a lang "jsonld" and url: "http://localhost:10101/302-redirects-infinite-loop-1"
    Then An StartingNodeNotFoundException is Thrown indicating that I'm in an infinite loop