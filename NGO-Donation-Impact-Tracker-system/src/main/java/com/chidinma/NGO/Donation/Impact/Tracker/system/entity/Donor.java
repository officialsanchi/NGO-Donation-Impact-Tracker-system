package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;

import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.DonorType;
import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Table(name = "donors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String donorNumber; // D-2024-00001

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonorType donorType;

    // Personal Information
    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private String organizationName; // For corporate/foundation donors

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(length = 1000)
    private String address;

    private String city;
    private String state;
    private String postalCode;
    private String country;

    // Tax/Compliance Information
    private String taxId; // SSN/EIN/Tax ID for receipts

    private Boolean taxReceiptRequired = true;

    private String preferredCurrency;

    // Engagement Tracking
    private LocalDate firstDonationDate;

    private LocalDate lastDonationDate;

    private Integer totalDonations;

    @Column(precision = 19, scale = 2)
    private java.math.BigDecimal lifetimeValue;

    private String donorSegment; // MAJOR, MID_LEVEL, MINOR, Lapsed, etc.

    private String communicationPreference; // EMAIL, MAIL, PHONE

    private Boolean anonymousDonor = false;

    private Boolean newsletterSubscriber = true;

    private String interests; // JSON array of cause areas

    private String referralSource; // How they found the NGO

    // Account
    private String passwordHash;

    private Boolean emailVerified = false;

    private LocalDateTime emailVerifiedAt;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.DONOR;

    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL)
    private List<Donation> donations = new ArrayList<>();

    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL)
    private List<RecurringDonation> recurringDonations = new ArrayList<>();

    @OneToMany(mappedBy = "donor", cascade = CascadeType.ALL)
    private List<DonorNote> notes = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime lastLogin;

    // Engagement scoring
    private Integer engagementScore; // 0-100 calculated field

    public String getDisplayName() {
        if (organizationName != null && !organizationName.isEmpty()) {
            return organizationName;
        }
        return firstName + " " + (lastName != null ? lastName : "");
    }

    public void recalculateLifetimeValue() {
        this.lifetimeValue = donations.stream()
                .filter(d -> d.getStatus() == com.ngo.tracker.enums.DonationStatus.COMPLETED)
                .map(Donation::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        this.totalDonations = (int) donations.stream()
                .filter(d -> d.getStatus() == com.ngo.tracker.enums.DonationStatus.COMPLETED)
                .count();

        // Update segment
        if (lifetimeValue.compareTo(new java.math.BigDecimal("10000")) >= 0) {
            this.donorSegment = "MAJOR";
        } else if (lifetimeValue.compareTo(new java.math.BigDecimal("1000")) >= 0) {
            this.donorSegment = "MID_LEVEL";
        } else if (lifetimeValue.compareTo(java.math.BigDecimal.ZERO) > 0) {
            this.donorSegment = "REGULAR";
        } else {
            this.donorSegment = "PROSPECT";
        }
    }
}
