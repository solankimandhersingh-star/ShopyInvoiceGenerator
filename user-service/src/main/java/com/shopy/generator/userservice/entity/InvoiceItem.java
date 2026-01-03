package com.shopy.generator.userservice.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private Double unitPrice;

    private Integer quantity;

    private Double lineTotal;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;
}

