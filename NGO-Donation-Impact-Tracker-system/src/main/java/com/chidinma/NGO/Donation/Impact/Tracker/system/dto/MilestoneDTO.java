package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MilestoneDTO {
    private Long id;
    private String milestoneName;
    private String description;
    private LocalDate targetDate;
    private String status;
    private BigDecimal budgetAllocation;
}
