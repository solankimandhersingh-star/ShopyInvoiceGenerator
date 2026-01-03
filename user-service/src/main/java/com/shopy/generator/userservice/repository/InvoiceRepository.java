package com.shopy.generator.userservice.repository;

import com.shopy.generator.userservice.entity.Invoice;
import com.shopy.generator.userservice.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByUserId(Long userId);

    long countByUserId(Long userId);

        List<Invoice> findByUser(UserAccount user);

}

