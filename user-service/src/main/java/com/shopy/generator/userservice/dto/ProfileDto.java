package com.shopy.generator.userservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDto {

    // USER
    private String username;
    private String mobile;

    // ORG
    private String name;
    private String email;
    private String phone;
    private String address;

    private String bankName;
    private String accountHolder;
    private String accountNumber;
    private String ifsc;
    private String upiId;

}
