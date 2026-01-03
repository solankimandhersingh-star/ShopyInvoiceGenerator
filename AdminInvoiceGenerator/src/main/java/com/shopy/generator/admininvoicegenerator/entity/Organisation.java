package com.shopy.generator.admininvoicegenerator.entity;

import com.shopy.generator.admininvoicegenerator.Constants.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String gstin;
    private String stateName;
    private String stateCode;
    private String statusCheck;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status = ApprovalStatus.PENDING;
}
