package com.shopy.generator.admininvoicegenerator.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class UsernameGenerator {
    private final SecureRandom random = new SecureRandom();

    public String generate() {
        int num = random.nextInt(1_000_000); // 0..999999
        return "IND" + String.format("%06d", num);
    }
}
