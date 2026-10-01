package com.personaldatastore.application.dto;

import java.time.LocalDate;

public record ExpenseRequest(
        String category,
        double amount,
        LocalDate expenseDate,
        boolean recurring,
        String notes
) {
}
