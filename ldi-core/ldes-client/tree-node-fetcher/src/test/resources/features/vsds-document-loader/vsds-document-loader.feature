Feature: VSDSDocumentLoader
  As a user
  I want to reuse the model that has a processed JSON-LD context in subsequent models

  Scenario: Configuring the JsonLdJava options
    Given I have a VSDSDocumentLoader
    When I configure the Jena Context to use the custom DocumentLoader
    And I modelize json ld content fetched from url "http://localhost:10101/200-1-relation-3-members"
    Then the configured DocumentLoader is used

