package com.chidinma.NGO.Donation.Impact.Tracker.system.enums;

public enum PaymentMethod {
    CREDIT_CARD("Credit/Debit Card"),
    BANK_TRANSFER("Bank Transfer/ACH"),
    PAYPAL("PayPal"),
    STRIPE("Stripe"),
    CHECK("Check/Money Order"),
    CRYPTO("Cryptocurrency"),
    WIRE("International Wire"),
    MOBILE_MONEY("Mobile Money (M-Pesa, etc.)"),
    CASH("Cash (In-Person)"),
    STOCK("Stock/Securities"),
    DAF("Donor Advised Fund");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
