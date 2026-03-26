package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.DonorType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DonorProfileResponse {
    private Long id;
    private String donorNumber;
    private String displayName;
    private DonorType donorType;
    private String email;
    private String country;
    private String donorSegment;
    private Integer totalDonations;
    private BigDecimal lifetimeValue;
    private String preferredCurrency;
    private Boolean newsletterSubscriber;
    private List<DonationSummary> recentDonations;
    private List<ProjectSubscription> followedProjects;
}
