Feature: Login

  Scenario Outline: Verify logging in with valid credential
    Given User is on Login page
    When User enter username "<username>" and password "<password>"
    Then Homepage is displayed
    @local
    Examples:
    |      username     |     password    |
    |   shop1@test.com  |     12345678    |

    @prod
    Examples:
      |       username      |     password    |
      |   demo@evershop.io  |      123456     |

  @local @prod
  Scenario: Verify logging in with invalid credentials
    Given User is on Login page
    When User login without entering username and password
    Then Error messages for email and password fields are displayed
    When User enter username "invalid email" and password "password"
    Then Error message "Invalid email" for email field is displayed
    When User enter username "invalid_account@example.com" and password "invalid password"
    Then Error message "Invalid email or password" is displayed
