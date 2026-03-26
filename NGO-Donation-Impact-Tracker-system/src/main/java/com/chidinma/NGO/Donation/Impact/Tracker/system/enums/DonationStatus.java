package com.chidinma.NGO.Donation.Impact.Tracker.system.enums;

public enum DonationStatus {
    PENDING("Payment Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    REFUNDED("Refunded"),
    CANCELLED("Cancelled"),
    DISPUTED("Under Dispute"),
    ON_HOLD("On Hold - Verification Required");

    private final String description;

    DonationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
