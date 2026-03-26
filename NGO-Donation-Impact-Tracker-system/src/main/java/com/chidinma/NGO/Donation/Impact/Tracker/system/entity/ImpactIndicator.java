package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;

import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.ImpactIndicatorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "impact_indicators")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactIndicator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String indicatorName;

    private String description;

    @Enumerated(EnumType.STRING)
    private ImpactIndicatorType indicatorType;

    private String unitOfMeasurement; // "people", "schools", "liters", "percentage"

    private Integer baselineValue;

    private Integer targetValue;

    private Integer actualValue;

    private LocalDate measurementDate;

    private String dataCollectionMethod;

    private String verificationSource;

    private String disaggregation; // By gender, age, etc.

    @OneToMany(mappedBy = "indicator", cascade = CascadeType.ALL)
    private List<ImpactMeasurement> measurements = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
}
