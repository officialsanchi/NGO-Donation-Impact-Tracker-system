package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "impact_measurements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImpactMeasurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id", nullable = false)
    private ImpactIndicator indicator;

    private Integer value;

    private LocalDate measurementPeriodStart;

    private LocalDate measurementPeriodEnd;

    private String notes;

    private String evidenceUrl; // Photo, document link

    private String measuredBy;

    private String verifiedBy;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
