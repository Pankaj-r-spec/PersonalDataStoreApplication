package com.personaldatastore.application.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotificationEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, Object payload) {
        kafkaTemplate.send(topic, payload);
    }
}
