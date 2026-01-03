package com.shopy.generator.notificationservice.dto;

public record InvoiceRequest(String email, String party, double amount) {}
