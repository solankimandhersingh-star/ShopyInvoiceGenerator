package com.shopy.generator.registrationservice.dto;

import lombok.Data;

@Data
public class RegistrationRequest {

    // user
    private String fullName;
    private String email;
    private String mobile;

    // organisation
    private String orgName;
    private String gstNumber;
    private String address;
    private String orgEmail;
    private String phone;
    private String stateCode;
    private String stateName;
    private String status;
}

