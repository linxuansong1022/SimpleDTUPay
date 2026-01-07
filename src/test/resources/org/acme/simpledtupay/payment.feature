Feature: Payment

  Scenario: Successful Payment
    Given a customer with name "Susan", last name "Baldwin", and CPR "010101-1111"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with Simple DTU Pay using their bank account
    Given a merchant with name "Daniel", last name "Oliver", and CPR "020202-2222"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with Simple DTU Pay using their bank account
    When the merchant initiates a payment for 10 kr by the customer
    Then the payment is successful
    And the balance of the customer at the bank is 990 kr
    And the balance of the merchant at the bank is 1010 kr

  Scenario: Customer is not known
    Given a merchant with name "Daniel", last name "Oliver", and CPR "020202-2222"
    And the merchant is registered with the bank with an initial balance of 1000 kr
    And the merchant is registered with Simple DTU Pay using their bank account
    When the merchant initiates a payment for 10 kr using customer id "non-existent-id"
    Then the payment is not successful
    And an error message is returned saying "customer with id \"non-existent-id\" is unknown"

  Scenario: Merchant is not known
    Given a customer with name "Susan", last name "Baldwin", and CPR "010101-1111"
    And the customer is registered with the bank with an initial balance of 1000 kr
    And the customer is registered with Simple DTU Pay using their bank account
    When the merchant initiates a payment for 10 kr by the customer using merchant id "non-existent-id"
    Then the payment is not successful
    And an error message is returned saying "merchant with id \"non-existent-id\" is unknown"
