package com.personaldatastore.application.dto;

import java.time.LocalDate;

public record InvestmentRequest(
        String name,
        String type,
        String category,
        double amount,
        double currentValue,
        LocalDate purchaseDate,
        String notes
) {
}
