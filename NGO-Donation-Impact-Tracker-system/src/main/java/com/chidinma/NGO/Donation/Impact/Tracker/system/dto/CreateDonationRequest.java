package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Data;

@Data
public class CreateDonationRequest {
    @NotNull
    private Long donorId;

    private Long projectId; // Optional - unrestricted if null

    @NotNull @Min(1)
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @NotNull
    private PaymentMethod paymentMethod;

    private Boolean isRecurring = false;

    private String recurringFrequency; // MONTHLY, QUARTERLY, ANNUALLY

    private Boolean isAnonymous = false;

    private String dedicationType;
    private String dedicationName;
    private String dedicationMessage;

    private String stripeToken; // For Stripe integration
    private String paypalOrderId; // For PayPal

    // Billing address (if different from donor profile)
    private String billingName;
    private String billingAddress;
    private String billingCity;
    private String billingPostalCode;
    private String billingCountry;
}
