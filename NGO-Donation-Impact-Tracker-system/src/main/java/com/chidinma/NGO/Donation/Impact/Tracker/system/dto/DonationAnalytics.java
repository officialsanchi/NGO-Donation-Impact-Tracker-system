package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DonationAnalytics {
    private BigDecimal totalRaised;
    private BigDecimal totalThisMonth;
    private BigDecimal totalThisYear;
    private Integer donationCount;
    private BigDecimal averageDonation;
    private BigDecimal recurringRevenue; // MRR
    private List<PaymentMethodBreakdown> byPaymentMethod;
    private List<CurrencyBreakdown> byCurrency;
    private List<TimeSeriesPoint> dailyTrends;
}
