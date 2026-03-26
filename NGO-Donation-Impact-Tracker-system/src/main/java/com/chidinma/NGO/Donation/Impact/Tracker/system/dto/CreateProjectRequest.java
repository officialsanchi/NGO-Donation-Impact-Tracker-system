package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Data;

@Data
public class CreateProjectRequest {
    @NotBlank
    private String projectName;

    @NotBlank
    private String description;

    private String sector;

    @NotBlank
    private String country;

    private String region;

    private String targetBeneficiaries;

    private Integer targetPopulation;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private BigDecimal totalBudget;

    private String implementingPartner;
}
