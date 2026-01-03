package com.shopy.generator.admininvoicegenerator.entity;

import com.shopy.generator.admininvoicegenerator.Constants.ApprovalStatus;
import com.shopy.generator.admininvoicegenerator.Constants.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    // auto: IND###### (6 digits)
    @Column(nullable = false)
    private String password;
    // stored encoded; initial raw "password"
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id")
    private Organisation organisation;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
    private boolean firstLogin = true;
    private boolean enabled = true;
}
