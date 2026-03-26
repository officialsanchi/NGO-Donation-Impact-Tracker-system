package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CurrencyBreakdown {
    private String currency;
    private BigDecimal amount;
    private BigDecimal amountInUsd;
}
