package com.shopy.generator.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;           // link to UserAccount

    private String fullName;
    private String organisationName;
    private String gst;
    private String address;
    private String email;
    private String phone;
    private String state;
    private String bankName;
    private String accountHolder;
    private String accountNumber;
    private String ifsc;
    private String upiId;

}

