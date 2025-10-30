Feature: Gmail Login using GitHub Account

  Scenario: Login to Gmail using GitHub credentials
    Given I navigate to Gmail login page
    When I click on GitHub login option
    And I enter GitHub credentials
    Then I should be logged into Gmail successfully