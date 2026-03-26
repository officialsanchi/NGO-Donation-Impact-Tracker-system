package com.chidinma.NGO.Donation.Impact.Tracker.system.enums;

public enum ProjectStatus {
    PLANNING("Planning Phase"),
    FUNDRAISING("Active Fundraising"),
    IMPLEMENTATION("Implementation"),
    MONITORING("Monitoring & Evaluation"),
    COMPLETED("Completed"),
    SUSPENDED("Temporarily Suspended"),
    CANCELLED("Cancelled"),
    EXTENDED("Extended Timeline");

    private final String description;

    ProjectStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
