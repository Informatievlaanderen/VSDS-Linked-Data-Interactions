Feature: MemberSupplier
  As a user
  I want get a stream of Members from the MemberSupplier

  Scenario Outline: Obtaining the members from first three fragments including the starting node
    Given A starting url "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances"
    And a <memberRepository> and a <treeNodeRecordRepository>
    When I create a Processor
    When I create a MemberSupplier
    When I request the 1 members from the MemberSupplier
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances"
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"
    Then Status "NOT_VISITED" for TreeNodeRecord with identifier: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:37:18.577Z"
    When I request the 249 members from the MemberSupplier
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances"
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"
    Then Status "NOT_VISITED" for TreeNodeRecord with identifier: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:37:18.577Z"
    When I request the 1 members from the MemberSupplier
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances"
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:36:09.72Z"
    Then Status "IMMUTABLE" for TreeNodeRecord with identifier: "https://private-api.gipod.beta-vlaanderen.be/api/v1/ldes/mobility-hindrances?generatedAtTime=2020-12-28T09:37:18.577Z"

    Examples:
      | memberRepository         | treeNodeRecordRepository         |
      | InMemoryMemberRepository | InMemoryTreeNodeRecordRepository |
      | SqliteMemberRepository   | SqliteTreeNodeRepository         |