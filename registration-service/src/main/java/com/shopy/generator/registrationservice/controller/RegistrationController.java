package com.shopy.generator.registrationservice.controller;

import com.shopy.generator.registrationservice.dto.RegistrationRequest;
import com.shopy.generator.registrationservice.entity.Organisation;
import com.shopy.generator.registrationservice.entity.RegisteredUser;
import com.shopy.generator.registrationservice.repository.OrganisationRepository;
import com.shopy.generator.registrationservice.repository.RegisteredUserRepository;
import com.shopy.generator.registrationservice.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/registration")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final RegisteredUserRepository userRepository;
    private final OrganisationRepository organisationRepository;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(registrationService.register(request));
    }

    @GetMapping("/pending")
    public List<RegisteredUser> getPendingUsers() {
        return userRepository.findByStatus("PENDING");
    }

    @GetMapping("/{id}")
    public RegisteredUser getById(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @PostMapping("/{id}/approve")
    public void approve(@PathVariable Long id) {

        RegisteredUser reg = userRepository.findById(id).orElseThrow();
        reg.setStatus("APPROVED");

        userRepository.save(reg);
    }

    @GetMapping("/orgs/pending")
    public List<Organisation> getPendingOrgs() {
        return organisationRepository.findByStatus("PENDING");
    }

    @PostMapping("/orgs/{id}/approve")
    public void approveOrg(@PathVariable Long id) {

        Organisation org = organisationRepository.findById(id).orElseThrow();

        org.setStatus("APPROVED");

        organisationRepository.save(org);
    }

    @GetMapping
    public String info() {
        return "Registration Service is running";
    }

}

