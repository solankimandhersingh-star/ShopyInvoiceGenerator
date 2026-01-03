package com.shopy.generator.admininvoicegenerator.repository;

import com.shopy.generator.admininvoicegenerator.entity.Invoice;
import com.shopy.generator.admininvoicegenerator.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByOrganisation(Organisation organisation);
}
