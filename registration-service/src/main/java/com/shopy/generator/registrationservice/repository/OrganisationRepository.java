package com.shopy.generator.registrationservice.repository;

import com.shopy.generator.registrationservice.entity.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
    List<Organisation> findByStatus(String status);
}
