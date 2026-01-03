package com.shopy.generator.userservice.repository;

import com.shopy.generator.userservice.entity.ForgotOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ForgotOtpRepository extends JpaRepository<ForgotOtp, Long> {
    Optional<ForgotOtp> findByUsername(String username);


    void deleteByUsername(String username);
}

