package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;

import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.Currency;
import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.DonationStatus;
import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.PaymentMethod;
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
@Table(name = "donations", indexes = {
        @Index(name = "idx_donation_donor", columnList = "donor_id"),
        @Index(name = "idx_donation_status", columnList = "status"),
        @Index(name = "idx_donation_date", columnList = "donation_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transactionId; // Internal reference

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project designatedProject; // If donor designated specific project

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private FundraisingCampaign campaign; // If through specific campaign

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    @Column(precision = 19, scale = 2)
    private BigDecimal amountInUsd; // Converted for reporting

    @Column(precision = 19, scale = 2)
    private BigDecimal feeAmount; // Payment processor fees

    @Column(precision = 19, scale = 2)
    private BigDecimal netAmount; // Amount after fees

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DonationStatus status = DonationStatus.PENDING;

    private String paymentGateway; // Stripe, PayPal, etc.

    private String gatewayTransactionId; // External reference

    private String gatewayResponse; // JSON response from payment processor

    private Boolean isRecurring = false;

    private Integer recurringDonationId; // Link to recurring plan

    private Boolean isAnonymous = false; // Public recognition preference

    private Boolean isMatchingGift = false; // Corporate matching

    private String matchingCompanyName;

    private BigDecimal matchingAmount;

    private String dedicationType; // IN_MEMORY, IN_HONOR

    private String dedicationName;

    private String dedicationMessage;

    private String designation; // UNRESTRICTED, PROJECT_SPECIFIC, EMERGENCY_FUND

    private LocalDate donationDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    private LocalDateTime receiptSentAt;

    private String receiptNumber;

    private String ipAddress; // For fraud detection

    private String userAgent; // Device information

    // Soft delete for audit trail
    private Boolean isDeleted = false;

    private String deletionReason;

    public void calculateNetAmount() {
        if (this.feeAmount != null && this.amount != null) {
            this.netAmount = this.amount.subtract(this.feeAmount);
        } else {
            this.netAmount = this.amount;
        }
    }
}
