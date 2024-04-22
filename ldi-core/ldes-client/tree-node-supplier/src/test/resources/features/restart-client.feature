Feature: Restart MemberSupplier
  As a user
  I want to stop and restart the MemberSupplier and use the persistent state

  Scenario Outline: Obtaining the members from first three fragments including the starting node
    Given A starting url "http://localhost:10101/302-redirects-to-first-node"
    And a StatePersistenceStrategy <statePersistenceStrategy>
    And The TreeNode is not processed: "http://localhost:10101/200-first-tree-node"
    When I create a Processor
    When I create a MemberSupplier with state
    When I request one member from the MemberSupplier
    Then Status "IMMUTABLE_WITH_UNPROCESSED_MEMBERS" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "NOT_VISITED" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/1" is processed
    Then MemberSupplier is destroyed
# Restart
    When I create a MemberSupplier with state
    When I request one member from the MemberSupplier
    Then Status "IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "MUTABLE_AND_ACTIVE" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/2" is processed
    Then MemberSupplier is destroyed
# Restart
    When I create a MemberSupplier without state
    When I request one member from the MemberSupplier
    Then Status "IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "MUTABLE_AND_ACTIVE" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/3" is processed
    Then MemberSupplier is destroyed

    Examples:
      | statePersistenceStrategy |
      | SQLITE                   |
      | POSTGRES                 |