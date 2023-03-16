Feature: MemberSupplier
  As a user
  I want get a stream of Members from the MemberSupplier

  Scenario Outline: Obtaining the members from first three fragments including the starting node
    Given A starting url "http://localhost:10101/302-redirects-to-first-node"
    And a StatePersistenceStrategy <statePersistenceStrategy>
    When I create a Processor
    Then Status "NOT_VISITED" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/1" is not processed
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/2" is not processed
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/3" is not processed
    When I create a MemberSupplier
    When I request the 1 members from the MemberSupplier
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "NOT_VISITED" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/1" is processed
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/2" is not processed
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/3" is not processed
    When I request the 1 members from the MemberSupplier
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "MUTABLE_AND_ACTIVE" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/1" is processed
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/2" is processed
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/3" is not processed
    When I request the 1 members from the MemberSupplier
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "MUTABLE_AND_ACTIVE" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/1" is processed
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/2" is processed
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/3" is processed

    Examples:
      | statePersistenceStrategy |
      | MEMORY                   |
      | SQLITE                   |