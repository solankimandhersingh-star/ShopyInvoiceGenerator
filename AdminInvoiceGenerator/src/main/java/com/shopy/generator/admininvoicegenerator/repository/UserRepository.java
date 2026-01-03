package com.shopy.generator.admininvoicegenerator.repository;

import com.shopy.generator.admininvoicegenerator.Constants.ApprovalStatus;
import com.shopy.generator.admininvoicegenerator.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByUsername(String username);

    List<UserAccount> findByApprovalStatus(ApprovalStatus status);

}
