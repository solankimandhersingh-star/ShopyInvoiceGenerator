package com.shopy.generator.admininvoicegenerator.service;

import com.shopy.generator.admininvoicegenerator.entity.RegisteredUser;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RegistrationClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public RegisteredUser[] getPendingRegistrations() {

        return restTemplate.getForObject(
                "http://localhost:8080/registration/pending",
                RegisteredUser[].class
        );
    }
}

