package com.personaldatastore.application.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    private final PortfolioService portfolioService;

    public NotificationConsumer(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @KafkaListener(topics = {"personal-data-notifications", "portfolio-events"}, groupId = "personal-data-store-group")
    public void consume(Map<String, Object> payload) {
        String event = String.valueOf(payload.getOrDefault("event", "notification"));
        String message = buildMessage(event, payload);
        log.info("Kafka event consumed: {} -> {}", event, payload);
        portfolioService.saveNotification(event, message);
    }

    private String buildMessage(String event, Map<String, Object> payload) {
        return switch (event) {
            case "investment.created" ->
                    "Investment created: " + payload.getOrDefault("name", "Unknown") + " for ₹" + payload.getOrDefault("amount", "0");
            case "expense.created" ->
                    "Expense added: " + payload.getOrDefault("category", "Unknown") + " for ₹" + payload.getOrDefault("amount", "0");
            default -> "Notification received at " + LocalDateTime.now();
        };
    }
}
