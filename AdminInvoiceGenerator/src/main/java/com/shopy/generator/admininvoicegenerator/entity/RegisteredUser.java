package com.shopy.generator.admininvoicegenerator.entity;

import lombok.Data;

@Data
public class RegisteredUser {

    private Long id;
    private String fullName;
    private String email;
    private String mobile;

    private Organisation organisation;
    private String status;


}

