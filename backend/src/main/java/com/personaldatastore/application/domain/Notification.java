package com.personaldatastore.application.domain;

import java.time.LocalDateTime;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Notification")
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    @Property("type")
    private String type;

    @Property("message")
    private String message;

    @Property("createdAt")
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(String type, String message, LocalDateTime createdAt) {
        this.type = type;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
