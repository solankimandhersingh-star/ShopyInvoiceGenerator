package com.shopy.generator.userservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;

    private String invoiceSeries;   // MAH
    private Integer invoiceSeq;     // 1,2,3...


    private LocalDate date;

    private String clientName;
    private String clientEmail;
    private String clientPhone;

    private Double subtotal;          // price * qty
    private Double cgstPercent;
    private Double sgstPercent;
    private Double igstPercent;

    private Double cgstAmount;
    private Double sgstAmount;
    private Double igstAmount;

    private Double discountPercent;
    private Double discountAmount;

    private Double totalAmount;       // final payable

    private String status;

    @ManyToOne
    private UserAccount user;

    @Builder.Default
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

}



