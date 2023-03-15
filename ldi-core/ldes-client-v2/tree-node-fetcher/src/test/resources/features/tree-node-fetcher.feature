Feature: TreeNodeFetcher
  As a user
  I want to use the TreeNodeFetcher to retrieve a Tree Node

  Scenario: Fetching an available TreeNode
    Given I have a TreeNodeFetcher
    When I create a TreeNodeRequest with Lang "jsonld" and url "http://localhost:10101/200-1-relation-3-members" and etag ""
    And I fetch the TreeNode
    Then the obtained TreeNode has 3 members and 1 relations

  Scenario: Fetching a redirect
    Given I have a TreeNodeFetcher
    When I create a TreeNodeRequest with Lang "turtle" and url "http://localhost:10101/302-redirects" and etag ""
    And I fetch the TreeNode
    Then the obtained TreeNode has 0 members and 1 relations

  Scenario: Fetching a cached Response
    Given I have a TreeNodeFetcher
    When I create a TreeNodeRequest with Lang "turtle" and url "http://localhost:10101/304-cached" and etag "1e0d1c-54e36ac89d1c0"
    And I fetch the TreeNode
    Then the obtained TreeNode has 0 members and 0 relations

  Scenario: Fetching a not-found Response
    Given I have a TreeNodeFetcher
    When I create a TreeNodeRequest with Lang "turtle" and url "http://localhost:10101/404-not-found" and etag ""
    Then An UnSupportedOperationException is thrown
