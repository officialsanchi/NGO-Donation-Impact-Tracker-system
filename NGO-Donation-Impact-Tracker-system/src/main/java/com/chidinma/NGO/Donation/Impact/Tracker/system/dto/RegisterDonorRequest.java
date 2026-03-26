package com.chidinma.NGO.Donation.Impact.Tracker.system.dto;

import lombok.Data;

@Data
public class RegisterDonorRequest {
    @NotNull
    private DonorType donorType;

    @NotBlank
    private String firstName;

    private String lastName;

    private String organizationName;

    @NotBlank @Email
    private String email;

    private String phone;

    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private String password;

    private String preferredCurrency;

    private Boolean anonymousDonor = false;

    private String interests;
}
