package com.shopy.generator.poratlservice.dto;

import lombok.Data;

@Data
public class RegistrationForm {
    private String fullName;
    private String email;
    private String mobile;

    private String orgName;
    private String gstNumber;
    private String address;
    private String orgEmail;
    private String phone;
    private String stateName;
    private String stateCode;
}

