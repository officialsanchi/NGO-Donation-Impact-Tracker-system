package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.DonationStatus;
import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DonationResponse {
    private Long id;
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private DonationStatus status;
    private PaymentMethod paymentMethod;
    private String gatewayTransactionId;
    private String receiptUrl;
    private String thankYouMessage;
}
