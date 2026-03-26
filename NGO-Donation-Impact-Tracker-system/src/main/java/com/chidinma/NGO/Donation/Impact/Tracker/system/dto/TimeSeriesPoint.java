package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TimeSeriesPoint {
    private String date;
    private BigDecimal amount;
    private Integer count;
}
