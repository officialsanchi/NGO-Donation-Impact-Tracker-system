package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.ProjectStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class ProjectResponse {
    private Long id;
    private String projectCode;
    private String projectName;
    private String description;
    private String sector;
    private String country;
    private String region;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalBudget;
    private BigDecimal fundsRaised;
    private BigDecimal fundsSpent;
    private BigDecimal fundsRemaining;
    private Double fundingPercentage;
    private Integer targetPopulation;
    private Integer actualPopulationReached;
    private List<MilestoneDTO> milestones;
    private List<ImpactIndicatorDTO> impactIndicators;
    private List<ProjectUpdateDTO> recentUpdates;
}
