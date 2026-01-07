package org.acme.simpledtupay.domain;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.simpledtupay.infrastructure.BankServiceAdapter;

import java.math.BigDecimal;
import java.util.*;

@ApplicationScoped
public class PaymentService {
    private Map<String,Customer> customers = new HashMap<>();
    private Map<String,Merchant> merchants = new HashMap<>();
    private List<Payment> payments = new ArrayList<>();

    @Inject
    BankServiceAdapter bankServiceAdapter;

    public String registerCustomer(Customer customer){
        String id = UUID.randomUUID().toString();
        customer.setId(id);
        //saved into map
        customers.put(id, customer);
        return id;
    }
    public String registerMerchant(Merchant merchant){
        String id = UUID.randomUUID().toString();
        merchant.setId(id);
        //saved into map
        merchants.put(id, merchant);
        return id;
    }
    public boolean pay(BigDecimal amount, String customerId, String merchantId, String description){
        Customer customer = customers.get(customerId);
        Merchant merchant = merchants.get(merchantId);
        if (customer == null) {
            throw new IllegalArgumentException("customer with id \"" + customerId + "\" is unknown");
        }
        if (merchant == null) {
            throw new IllegalArgumentException("merchant with id \"" + merchantId + "\" is unknown");
        }
        bankServiceAdapter.transferMoney(
                customer.getBankAccountNumber(),
                merchant.getBankAccountNumber(),
                amount,
                description
        );

        Payment payment = new Payment(amount, customerId, merchantId, description);
        payments.add(payment);
        return true;
    }
    public List<Payment> getPayments(){
        return payments;
    }
}
