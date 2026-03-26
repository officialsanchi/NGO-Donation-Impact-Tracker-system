package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProjectSubscription {
    private Long projectId;
    private String projectName;
    private String country;
    private BigDecimal donatedAmount;
    private String latestUpdate;
}
