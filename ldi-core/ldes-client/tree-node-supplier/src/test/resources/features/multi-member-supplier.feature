Feature: MemberSupplier
  As a user
  I want have multiple Member Suppliers running in Parallel

  Scenario Outline: Obtaining the members from first three fragments including the starting node in two memberSuppliers
    Given A starting url "http://localhost:10101/302-redirects-to-first-node"
    And a <statePersistenceStrategy1> MemberSupplier and a <statePersistenceStrategy2> MemberSupplier
    And The TreeNode is not processed: "http://localhost:10101/200-first-tree-node"
    When I request one member from the MemberSuppliers
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/1" is processed in both MemberSuppliers
    When I request one member from the MemberSuppliers
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/2" is processed in both MemberSuppliers
    When I request one member from the MemberSuppliers
    Then Member "https://private-api.gipod.beta-vlaanderen.be/api/v1/mobility-hindrances/3" is processed in both MemberSuppliers
    Then MemberSuppliers are destroyed

    Examples:
      | statePersistenceStrategy1 | statePersistenceStrategy2 |
      | MEMORY                    | MEMORY                    |
      | SQLITE                    | SQLITE                    |
      | POSTGRES                  | POSTGRES                  |
      | SQLITE                    | POSTGRES                  |