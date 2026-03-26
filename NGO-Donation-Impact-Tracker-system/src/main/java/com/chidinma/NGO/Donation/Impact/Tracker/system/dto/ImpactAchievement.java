package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImpactAchievement {
    private String indicatorName;
    private Integer target;
    private Integer actual;
    private String narrative;
}
