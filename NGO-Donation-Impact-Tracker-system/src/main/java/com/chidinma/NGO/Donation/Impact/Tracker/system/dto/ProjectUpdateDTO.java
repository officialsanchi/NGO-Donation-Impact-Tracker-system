package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ProjectUpdateDTO {
    private Long id;
    private String updateTitle;
    private String updateContent;
    private String updateType;
    private List<String> mediaUrls;
    private LocalDate createdAt;
}
