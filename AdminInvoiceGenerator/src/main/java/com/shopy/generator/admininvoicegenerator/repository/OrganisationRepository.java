package com.shopy.generator.admininvoicegenerator.repository;


import com.shopy.generator.admininvoicegenerator.Constants.ApprovalStatus;
import com.shopy.generator.admininvoicegenerator.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    Optional<Organisation> findByName(String name);

    List<Organisation> findByStatus(ApprovalStatus status);
    List<Organisation> findByNameContainingIgnoreCase(String name);

}
