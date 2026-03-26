package com.chidinma.NGO.Donation.Impact.Tracker.system.enums;

public enum DonorType {
    INDIVIDUAL("Individual Donor"),
    CORPORATE("Corporate Partner"),
    FOUNDATION("Private Foundation"),
    GOVERNMENT("Government/Agency"),
    INSTITUTIONAL("Institutional Funder"),
    LEGACY("Legacy/Planned Giving"),
    RECURRING("Monthly Sustainer");

    private final String displayName;

    DonorType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
