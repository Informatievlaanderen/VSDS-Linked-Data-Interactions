Feature: MemberSupplier
  As a user
  I want get a stream of Members from the MemberSupplier

  Scenario Outline: Obtaining the members from first three fragments including the starting node
    Given A starting url "http://localhost:10101/302-redirects-to-first-node"
    And a StatePersistenceStrategy <statePersistenceStrategy>
    And The TreeNode is not processed: "http://localhost:10101/200-first-tree-node"
    When I create a Processor
    When I create a MemberSupplier without state
    When I request one member from the MemberSupplier
    Then Status "IMMUTABLE_WITH_UNPROCESSED_MEMBERS" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "NOT_VISITED" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/1" is processed
    When I request one member from the MemberSupplier
    Then Status "IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "MUTABLE_AND_ACTIVE" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/2" is processed
    When I request one member from the MemberSupplier
    Then Status "IMMUTABLE_WITHOUT_UNPROCESSED_MEMBERS" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "MUTABLE_AND_ACTIVE" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/3" is processed
    Then MemberSupplier is destroyed

    Examples:
      | statePersistenceStrategy |
      | MEMORY                   |
      | SQLITE                   |
      | POSTGRES                 |



  Scenario Outline: Obtaining the members from first three fragments including the starting node in order
    Given A starting url "http://localhost:10101/200-tree-node-not-ordered"
    And a StatePersistenceStrategy <statePersistenceStrategy>
    And The TreeNode is not processed: "http://localhost:10101/200-tree-node-not-ordered"
    And I set a timestamp path "http://www.w3.org/ns/prov#generatedAtTime"
    When I create a Processor
    When I create a MemberSupplier without state
    When I request one member from the MemberSupplier
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/2" is processed
    When I request one member from the MemberSupplier
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/1" is processed
    Then MemberSupplier is destroyed

    Examples:
      | statePersistenceStrategy |
      | MEMORY                   |
      | SQLITE                   |
      | POSTGRES                 |

  Scenario Outline: Obtaining the members from multiple endpoints
    Given Starting urls
      | http://localhost:10101/items/grouped?group=1 |
      | http://localhost:10101/items/grouped?group=2 |
    And a StatePersistenceStrategy <statePersistenceStrategy>
    And I set a timestamp path "http://www.w3.org/ns/prov#generatedAtTime"
    And The TreeNode is not processed: "http://localhost:10101/items/grouped?group=1"
    And The TreeNode is not processed: "http://localhost:10101/items/grouped?group=2"
    When I create a Processor
    When I create a MemberSupplier without state
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/1" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/2" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/3" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/4" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/5" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/6" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/7" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/8" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/9" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/10" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/11" is processed
    When I request one member from the MemberSupplier
    Then Member "http://localhost:10101/items/12" is processed
    Then MemberSupplier is destroyed

    Examples:
      | statePersistenceStrategy |
      | MEMORY                   |
      | SQLITE                   |
      | POSTGRES                 |


  Scenario Outline: Obtaining the members with the exactly once filter
    Given A starting url "http://localhost:10101/200-first-tree-node-to-duplicate"
    And a StatePersistenceStrategy <statePersistenceStrategy>
    And The TreeNode is not processed: "http://localhost:10101/200-first-tree-node-to-duplicate"
    And I set a timestamp path "http://www.w3.org/ns/prov#generatedAtTime"
    And I create a Processor
    And I create a MemberSupplier with ExactlyOnceFilter
    When I request one member from the MemberSupplier
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/1" is processed
    When I request one member from the MemberSupplier
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/2" is processed
    Then MemberSupplier is destroyed

    Examples:
      | statePersistenceStrategy |
      | MEMORY                   |
      | SQLITE                   |
      | POSTGRES                 |

  Scenario Outline: Obtaining the members with the latest state filter
    Given A starting url "http://localhost:10101/200-tree-node-without-relations"
    And a StatePersistenceStrategy <statePersistenceStrategy>
    And The TreeNode is not processed: "http://localhost:10101/200-first-tree-node-to-duplicate"
    And I set a timestamp path "http://www.w3.org/ns/prov#generatedAtTime"
    When I create a Processor
    When I create a MemberSupplier with LatestStateFilter
    When I request one member from the MemberSupplier
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/1" is processed
    When I request one member from the MemberSupplier
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/2" is processed
    Then MemberSupplier is destroyed

    Examples:
      | statePersistenceStrategy |
      | MEMORY                   |
      | SQLITE                   |
      | POSTGRES                 |