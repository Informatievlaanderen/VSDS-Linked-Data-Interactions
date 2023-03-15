Feature: MemberSupplier
  As a user
  I want get a stream of Members from the MemberSupplier

  Scenario Outline: Obtaining the members from first three fragments including the starting node
    Given A starting url "http://localhost:10101/302-redirects-to-first-node"
    And a <memberRepository> and a <treeNodeRecordRepository>
    When I create a Processor
    When I create a MemberSupplier
    When I request the 1 members from the MemberSupplier
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "http://localhost:10101/302-redirects-to-first-node"
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "NOT_VISITED" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    When I request the 249 members from the MemberSupplier
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "http://localhost:10101/302-redirects-to-first-node"
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "NOT_VISITED" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"
    When I request the 1 members from the MemberSupplier
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "http://localhost:10101/302-redirects-to-first-node"
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "http://localhost:10101/200-first-tree-node"
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "http://localhost:10101/200-second-tree-node"

    Examples:
      | memberRepository         | treeNodeRecordRepository         |
      | InMemoryMemberRepository | InMemoryTreeNodeRecordRepository |
      | SqliteMemberRepository   | SqliteTreeNodeRepository         |