package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentIntentResponse {
    private String clientSecret; // For Stripe Elements
    private String paymentIntentId;
    private BigDecimal amount;
    private String currency;
    private String status;
}
