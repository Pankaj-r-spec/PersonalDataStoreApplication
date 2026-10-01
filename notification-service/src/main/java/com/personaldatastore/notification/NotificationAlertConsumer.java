package com.personaldatastore.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class NotificationAlertConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationAlertConsumer.class);

    private final AlertNotificationProcessor alertNotificationProcessor;
    private final AlertNotificationRepository repository;

    public NotificationAlertConsumer(AlertNotificationProcessor alertNotificationProcessor,
                                    AlertNotificationRepository repository) {
        this.alertNotificationProcessor = alertNotificationProcessor;
        this.repository = repository;
    }

    @KafkaListener(topics = {"portfolio-events", "personal-data-notifications"}, groupId = "notification-service-group")
    public void consume(Map<String, Object> payload) {
        String eventType = String.valueOf(payload.getOrDefault("event", payload.getOrDefault("eventType", "notification")));
        String message = alertNotificationProcessor.buildMessage(payload);

        log.info("Consumed Kafka event [{}]: {}", eventType, payload);

        AlertNotification notification = new AlertNotification(
                eventType,
                message,
                "IN_APP",
                LocalDateTime.now()
        );

        repository.save(notification);
    }
}
