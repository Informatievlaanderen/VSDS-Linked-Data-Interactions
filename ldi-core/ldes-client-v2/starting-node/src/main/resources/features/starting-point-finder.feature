Feature: StartingTreeNodeFinder
  As a user
  I want to use the StartingTreeNodeFinder to determine the starting Tree Node of an LDES Stream

  Scenario: Determining the starting TreeNode Starting from a View
    Given I have a StartingTreeNodeFinder
    When I provide the endpoint of a Tree Node that is also a View: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"
    Then the starting Tree Node of the LDES Stream is the url of the View: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"

  Scenario: Determining the starting TreeNode Starting from an endpoint that redirects to a View
    Given I have a StartingTreeNodeFinder
    When I provide an endpoint that redirects to a Tree Node that is also a View: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances"
    Then the starting Tree Node of the LDES Stream is the url of the View: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"