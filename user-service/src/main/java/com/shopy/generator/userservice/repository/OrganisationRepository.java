package com.shopy.generator.userservice.repository;


import com.shopy.generator.userservice.dto.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganisationRepository extends JpaRepository<Organisation, Long> {
//    Organisation findByUserId(Long userId);
}
