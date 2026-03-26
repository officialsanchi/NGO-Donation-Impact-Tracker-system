package com.chidinma.NGO.Donation.Impact.Tracker.system.entity;

import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.Currency;
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
@Table(name = "recurring_donations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project designatedProject;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Currency currency;

    private String frequency; // MONTHLY, QUARTERLY, ANNUALLY

    private Integer dayOfMonth; // For monthly: 1-28

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    private String paymentToken; // Secure token from gateway

    private String gatewayCustomerId;

    private String gatewaySubscriptionId;

    private LocalDate startDate;

    private LocalDate endDate; // Null if ongoing

    private LocalDate nextChargeDate;

    private String status; // ACTIVE, PAUSED, CANCELLED, EXPIRED

    private Integer successfulCharges;

    private Integer failedCharges;

    private BigDecimal totalAmountCharged;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime cancelledAt;

    private String cancellationReason;
}
