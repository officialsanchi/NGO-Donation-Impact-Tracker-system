package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImpactIndicatorDTO {
    private Long id;
    private String indicatorName;
    private String description;
    private String indicatorType;
    private String unitOfMeasurement;
    private Integer baselineValue;
    private Integer targetValue;
    private Integer actualValue;
    private Double achievementPercentage;
}
