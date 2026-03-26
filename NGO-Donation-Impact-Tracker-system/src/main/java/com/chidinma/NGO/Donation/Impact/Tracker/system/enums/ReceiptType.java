package com.chidinma.NGO.Donation.Impact.Tracker.system.enums;

public enum ReceiptType {
    TAX_RECEIPT("Official Tax Receipt"),
    ACKNOWLEDGMENT("Donation Acknowledgment"),
    IMPACT_REPORT("Impact Report"),
    ANNUAL_SUMMARY("Annual Giving Summary");

    private final String description;

    ReceiptType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
