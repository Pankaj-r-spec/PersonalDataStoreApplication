# Notification & Alerting Service

This microservice is responsible for consuming Kafka event streams from the core ledger service and turning them into user-facing alerts.

## Responsibilities

- Listen to `portfolio-events` and `personal-data-notifications`
- Build alert messages for investments, expenses, and portfolio changes
- Persist alerts in Neo4j as graph nodes
- Provide a dedicated processing boundary for notification logic

## Local run

```bash
mvn spring-boot:run
```

Then the service listens on port 8082 and connects to the same Kafka and Neo4j infrastructure used by the main application.
