package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "project_milestones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMilestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String milestoneName;

    private String description;

    private LocalDate targetDate;

    private LocalDate actualDate;

    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED, DELAYED

    @Column(precision = 19, scale = 2)
    private BigDecimal budgetAllocation;

    private String deliverables;

    private String verificationMethod;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
