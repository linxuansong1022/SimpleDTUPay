package org.acme.simpledtupay;

import dtu.ws.fastmoney.*;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.acme.simpledtupay.domain.Customer;
import org.acme.simpledtupay.domain.Merchant;
import org.acme.simpledtupay.api.PaymentResource.PaymentRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SimpleDTUPaySteps {

    // 1. Data Holders
    private Customer customer;
    private Merchant merchant;
    private String customerBankId, merchantBankId;
    private String customerId, merchantId;

    // 2. Clients
    private SimpleDTUPayClient client = new SimpleDTUPayClient();
    private BankService bank = new BankService_Service().getBankServicePort();

    // 3. Test State
    private Response lastResponse;
    private List<String> createdBankAccounts = new ArrayList<>();

    private static final String BANK_API_KEY = "lemon3145";

    // -------------------------------------------------------------------------
    // CUSTOMER STEPS
    // -------------------------------------------------------------------------

    @Given("a customer with name {string}, last name {string}, and CPR {string}")
    public void aCustomerWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        customer = new Customer();
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setCprNumber(cpr);
    }

    @Given("the customer is registered with the bank with an initial balance of {bigdecimal} kr")
    public void theCustomerIsRegisteredWithTheBank(BigDecimal balance) throws BankServiceException_Exception {
        User user = new User();
        user.setFirstName(customer.getFirstName());
        user.setLastName(customer.getLastName());
        user.setCprNumber(customer.getCprNumber());

        customerBankId = bank.createAccountWithBalance(BANK_API_KEY, user, balance);
        createdBankAccounts.add(customerBankId);
    }

    @Given("the customer is registered with Simple DTU Pay using their bank account")
    public void theCustomerIsRegisteredWithSimpleDTUPay() {
        customer.setBankAccountNumber(customerBankId);
        customerId = client.registerCustomer(customer);
    }

    // -------------------------------------------------------------------------
    // MERCHANT STEPS
    // -------------------------------------------------------------------------

    @Given("a merchant with name {string}, last name {string}, and CPR {string}")
    public void aMerchantWithNameLastNameAndCPR(String firstName, String lastName, String cpr) {
        merchant = new Merchant();
        merchant.setFirstName(firstName);
        merchant.setLastName(lastName);
        merchant.setCprNumber(cpr);
    }

    @Given("the merchant is registered with the bank with an initial balance of {bigdecimal} kr")
    public void theMerchantIsRegisteredWithTheBank(BigDecimal balance) throws BankServiceException_Exception {
        User user = new User();
        user.setFirstName(merchant.getFirstName());
        user.setLastName(merchant.getLastName());
        user.setCprNumber(merchant.getCprNumber());

        merchantBankId = bank.createAccountWithBalance(BANK_API_KEY, user, balance);
        createdBankAccounts.add(merchantBankId);
    }

    @Given("the merchant is registered with Simple DTU Pay using their bank account")
    public void theMerchantIsRegisteredWithSimpleDTUPay() {
        merchant.setBankAccountNumber(merchantBankId);
        merchantId = client.registerMerchant(merchant);
    }

    // -------------------------------------------------------------------------
    // PAYMENT STEPS
    // -------------------------------------------------------------------------

    @When("the merchant initiates a payment for {bigdecimal} kr by the customer")
    public void theMerchantInitiatesAPayment(BigDecimal amount) {
        PaymentRequest request = new PaymentRequest();
        request.amount = amount;
        request.customerId = customerId;
        request.merchantId = merchantId;
        request.description = "Test Payment";

        lastResponse = client.pay(request);
    }

    @When("the merchant initiates a payment for {bigdecimal} kr using customer id {string}")
    public void theMerchantInitiatesAPaymentWithInvalidCustomerId(BigDecimal amount, String invalidCid) {
        PaymentRequest request = new PaymentRequest();
        request.amount = amount;
        request.customerId = invalidCid;
        request.merchantId = merchantId;
        request.description = "Test Invalid Customer";

        lastResponse = client.pay(request);
    }

    @When("the merchant initiates a payment for {bigdecimal} kr by the customer using merchant id {string}")
    public void theMerchantInitiatesAPaymentWithInvalidMerchantId(BigDecimal amount, String invalidMid) {
        PaymentRequest request = new PaymentRequest();
        request.amount = amount;
        request.customerId = customerId;
        request.merchantId = invalidMid;
        request.description = "Test Invalid Merchant";

        lastResponse = client.pay(request);
    }

    // -------------------------------------------------------------------------
    // VERIFICATION STEPS
    // -------------------------------------------------------------------------

    @Then("the payment is successful")
    public void thePaymentIsSuccessful() {
        assertEquals(200, lastResponse.getStatusCode());
    }

    @Then("the payment is not successful")
    public void thePaymentIsNotSuccessful() {
        assertEquals(404, lastResponse.getStatusCode());
    }

    @Then("an error message is returned saying {string}")
    public void anErrorMessageIsReturnedSaying(String expectedMessage) {
        assertEquals(expectedMessage, lastResponse.getBody().asString());
    }

    @Then("the balance of the customer at the bank is {bigdecimal} kr")
    public void theBalanceOfTheCustomerAtTheBankIsKr(BigDecimal expectedBalance) throws BankServiceException_Exception {
        Account account = bank.getAccount(customerBankId);
        assertEquals(0, expectedBalance.compareTo(account.getBalance()));
    }

    @Then("the balance of the merchant at the bank is {bigdecimal} kr")
    public void theBalanceOfTheMerchantAtTheBankIsKr(BigDecimal expectedBalance) throws BankServiceException_Exception {
        Account account = bank.getAccount(merchantBankId);
        assertEquals(0, expectedBalance.compareTo(account.getBalance()));
    }

    // -------------------------------------------------------------------------
    // CLEANUP
    // -------------------------------------------------------------------------

    @After
    public void cleanup() {
        for (String accountId : createdBankAccounts) {
            try {
                bank.retireAccount(BANK_API_KEY, accountId);
            } catch (Exception e) {
                // Ignore cleanup errors
            }
        }
        createdBankAccounts.clear();
    }
}