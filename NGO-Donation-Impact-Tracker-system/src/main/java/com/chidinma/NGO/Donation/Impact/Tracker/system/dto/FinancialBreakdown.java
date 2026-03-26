package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FinancialBreakdown {
    private String category;
    private BigDecimal budgeted;
    private BigDecimal actual;
    private BigDecimal variance;
}
