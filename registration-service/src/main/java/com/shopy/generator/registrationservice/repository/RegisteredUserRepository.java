package com.shopy.generator.registrationservice.repository;

import com.shopy.generator.registrationservice.entity.RegisteredUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegisteredUserRepository extends JpaRepository<RegisteredUser, Long> {
    List<RegisteredUser> findByStatus(String status);

}
