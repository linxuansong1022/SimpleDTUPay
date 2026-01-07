package org.acme.simpledtupay.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private BigDecimal amount;
    private String customerId;
    private String merchantId;
    private String description;
}
