Feature: ArchiveFileInIntegrationTest
  As a user
  I want to be able to use a file archive as input for LDIO

  Scenario: Crawling a file archive
    Given I start an archive-file-in component with archive-dir "src/test/resources/archive" and no source-format
    Then All the members from the archive are passed to the pipeline in lexical order

