package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ImpactReportDTO {
    private String projectName;
    private String reportingPeriod;
    private BigDecimal fundsUtilized;
    private List<ImpactAchievement> achievements;
    private List<BeneficiaryStory> beneficiaryStories;
    private List<FinancialBreakdown> financialBreakdown;
    private String nextPeriodPlan;
}
