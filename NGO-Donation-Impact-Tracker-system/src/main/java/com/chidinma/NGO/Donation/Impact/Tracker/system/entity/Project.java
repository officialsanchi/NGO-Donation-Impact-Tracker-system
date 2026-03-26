package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;

import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String projectCode; // PRJ-2024-WASH-001

    @Column(nullable = false)
    private String projectName;

    @Column(length = 5000)
    private String description;

    private String sector; // WASH, Education, Health, Livelihoods, etc.

    private String country;

    private String region;

    private String targetBeneficiaries; // "Children under 5", "Refugee women", etc.

    private Integer targetPopulation;

    private Integer actualPopulationReached;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.PLANNING;

    private LocalDate startDate;

    private LocalDate endDate;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalBudget;

    @Column(precision = 19, scale = 2)
    private BigDecimal fundsRaised;

    @Column(precision = 19, scale = 2)
    private BigDecimal fundsSpent;

    @Column(precision = 19, scale = 2)
    private BigDecimal fundsRemaining;

    private String implementingPartner;

    private String donorReportFrequency; // MONTHLY, QUARTERLY, ANNUAL

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectMilestone> milestones = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ImpactIndicator> impactIndicators = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Donation> donations = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectUpdate> updates = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private String createdBy;

    public void recalculateFinancials() {
        this.fundsRaised = donations.stream()
                .filter(d -> d.getStatus() == com.ngo.tracker.enums.DonationStatus.COMPLETED)
                .map(Donation::getNetAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.fundsSpent = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.fundsRemaining = this.fundsRaised.subtract(this.fundsSpent);
    }

    public Double getFundingPercentage() {
        if (totalBudget == null || totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return fundsRaised.multiply(BigDecimal.valueOf(100))
                .divide(totalBudget, 2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }
}
