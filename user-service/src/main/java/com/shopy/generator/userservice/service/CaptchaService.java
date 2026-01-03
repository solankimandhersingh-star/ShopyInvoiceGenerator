package com.shopy.generator.userservice.service;


import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class CaptchaService {

    private String latest;

    public String generate() {
        latest = RandomStringUtils.randomAlphabetic(6).toUpperCase();
        return latest;
    }

    public boolean validate(String input) {
        return input != null && input.equalsIgnoreCase(latest);
    }
}
