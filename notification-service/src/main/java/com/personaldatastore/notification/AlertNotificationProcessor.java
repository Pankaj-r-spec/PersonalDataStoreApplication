package com.personaldatastore.notification;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AlertNotificationProcessor {

    public String buildMessage(Map<String, Object> payload) {
        String eventType = String.valueOf(payload.getOrDefault("event", payload.getOrDefault("eventType", "notification")));

        return switch (eventType) {
            case "investment.created" ->
                    "Investment created: " + payload.getOrDefault("name", "Unknown") + " for ₹" + payload.getOrDefault("amount", "0");
            case "investment.updated" ->
                    "Portfolio updated: " + payload.getOrDefault("name", "Investment") + " changed to ₹" + payload.getOrDefault("amount", "0");
            case "expense.created", "expense-created" -> buildExpenseMessage(payload, false);
            case "expense.updated", "expense-updated" -> buildExpenseMessage(payload, true);
            case "portfolio-updated", "portfolio.updated" ->
                    "Portfolio update: " + payload.getOrDefault("portfolioName", "Core portfolio") + " changed by " + payload.getOrDefault("changePercent", "0") + "%";
            default -> "Notification triggered at " + LocalDateTime.now();
        };
    }

    private String buildExpenseMessage(Map<String, Object> payload, boolean updated) {
        String category = String.valueOf(payload.getOrDefault("category", "Unknown"));
        double amount = toDouble(payload.get("amount"));
        double budget = toDouble(payload.get("monthlyBudget"));

        if (budget > 0 && amount > budget) {
            return "Budget alert: " + category + " expense exceeded the monthly budget. Amount ₹" + amount + " crossed the limit ₹" + budget + ".";
        }

        String prefix = updated ? "Expense updated" : "Expense created";
        return prefix + ": " + category + " for ₹" + amount;
    }

    private double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (Exception ex) {
            return 0d;
        }
    }
}
