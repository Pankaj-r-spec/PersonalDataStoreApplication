package com.personaldatastore.application.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import com.personaldatastore.application.domain.Notification;

public interface NotificationRepository extends Neo4jRepository<Notification, Long> {
}
