package org.acme.simpledtupay.domain;

import lombok.Data;

@Data
public class Customer {
    private String id;
    private String firstName;
    private String lastName;
    private String cprNumber;
    private String bankAccountNumber;
}
