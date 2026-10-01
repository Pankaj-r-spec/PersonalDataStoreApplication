package com.personaldatastore.notification;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;

@Node("AlertNotification")
public class AlertNotification {

    @Id
    @GeneratedValue
    private Long id;

    private String eventType;
    private String message;
    private String channel;
    private LocalDateTime createdAt;

    public AlertNotification() {
    }

    public AlertNotification(String eventType, String message, String channel, LocalDateTime createdAt) {
        this.eventType = eventType;
        this.message = message;
        this.channel = channel;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public String getMessage() {
        return message;
    }

    public String getChannel() {
        return channel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
