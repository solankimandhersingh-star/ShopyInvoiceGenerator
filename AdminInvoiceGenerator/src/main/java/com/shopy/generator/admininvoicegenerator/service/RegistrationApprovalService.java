package com.shopy.generator.admininvoicegenerator.service;

import com.shopy.generator.admininvoicegenerator.Constants.Role;
import com.shopy.generator.admininvoicegenerator.entity.RegisteredUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RegistrationApprovalService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AdminService userService;   // your existing admin user service



    public void approve(Long registrationId) {
    System.out.println("Strating to hit DB for data");
        // 1️⃣ Fetch registration details from registration-service
        RegisteredUser reg = restTemplate.getForObject(
                "http://localhost:8080/registration/" + registrationId,
                RegisteredUser.class
        );

        System.out.println("System start for registration details with id"+reg);

        // 2️⃣ Create user in admin DB
       var new_user =  userService.createUser(
                Role.USER,                                 // or Role.ADMIN if needed
                reg.getOrganisation().getName()
        );
        System.out.println("user is created succes fully "+ new_user.getUsername());

        // 3️⃣ Tell registration-service to mark APPROVED
        restTemplate.postForLocation(
                "http://localhost:8080/registration/" + registrationId + "/approve",
                null
        );
    }
}

