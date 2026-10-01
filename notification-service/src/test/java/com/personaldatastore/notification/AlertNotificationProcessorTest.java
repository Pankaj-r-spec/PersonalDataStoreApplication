package com.personaldatastore.notification;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AlertNotificationProcessorTest {

    @Test
    void shouldCreateBudgetAlertForHighExpense() {
        AlertNotificationProcessor processor = new AlertNotificationProcessor();

        String message = processor.buildMessage(Map.of(
                "eventType", "expense-created",
                "category", "Food",
                "amount", 2500.0,
                "monthlyBudget", 2000.0
        ));

        assertTrue(message.contains("Food"));
        assertTrue(message.contains("budget"));
        assertTrue(message.contains("2500.0"));
    }

    @Test
    void shouldCreatePortfolioUpdateMessage() {
        AlertNotificationProcessor processor = new AlertNotificationProcessor();

        String message = processor.buildMessage(Map.of(
                "eventType", "portfolio-updated",
                "portfolioName", "Core portfolio",
                "changePercent", 8.4
        ));

        assertTrue(message.contains("Core portfolio"));
        assertTrue(message.contains("8.4"));
    }
}
