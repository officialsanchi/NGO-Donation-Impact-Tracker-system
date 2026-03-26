package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "donor_notes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DonorNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @Column(nullable = false, length = 2000)
    private String note;

    private String category; // INTERACTION, PREFERENCE, COMPLAINT, FEEDBACK

    private String createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Boolean isPrivate = true;
}
