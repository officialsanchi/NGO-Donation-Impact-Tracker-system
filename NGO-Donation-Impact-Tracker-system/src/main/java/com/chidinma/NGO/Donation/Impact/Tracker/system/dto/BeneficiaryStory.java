package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BeneficiaryStory {
    private String title;
    private String story;
    private String photoUrl;
    private String location;
}
