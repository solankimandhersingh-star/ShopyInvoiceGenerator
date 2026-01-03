package com.shopy.generator.userservice.repository;

import com.shopy.generator.userservice.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByUserId(Long userId);
    long countByUserId(Long userId);
}
