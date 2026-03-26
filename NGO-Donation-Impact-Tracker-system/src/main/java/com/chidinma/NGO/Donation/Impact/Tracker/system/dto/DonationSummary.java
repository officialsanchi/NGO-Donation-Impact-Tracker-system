package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DonationSummary {
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private DonationStatus status;
    private LocalDate donationDate;
    private String projectName;
    private String receiptNumber;
}
