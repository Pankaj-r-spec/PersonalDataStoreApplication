package com.personaldatastore.notification;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertNotificationRepository extends Neo4jRepository<AlertNotification, Long> {
}
