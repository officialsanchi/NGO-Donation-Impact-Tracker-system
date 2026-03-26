package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import com.chidinma.NGO.Donation.Impact.Tracker.system.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentMethodBreakdown {
    private PaymentMethod method;
    private BigDecimal amount;
    private Integer count;
    private double percentage;
}
