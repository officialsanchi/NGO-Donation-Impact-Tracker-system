package com.chidinma.NGO.Donation.Impact.Tracker.system.enums;

public enum ImpactIndicatorType {
    OUTPUT("Output (Direct Products)"),
    OUTCOME("Outcome (Short-term Changes)"),
    IMPACT("Impact (Long-term Transformation)");

    private final String description;

    ImpactIndicatorType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
