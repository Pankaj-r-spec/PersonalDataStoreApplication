# Personal Data Store Application

A modern personal finance and life-data platform built with:

- React 19 + Vite (frontend)
- Spring Boot 3.4 + Java 21 (backend)
- Neo4j (graph database)
- Kafka (async eventing and notifications)
- Docker Compose (local infrastructure)

## Business scope

This application is designed to manage:

1. Investment tracking
   - Fixed deposits (FD)
   - Emergency fund
   - Equity and stock positions
   - Mutual funds and other assets
   - Total portfolio value updates on create, update, and delete operations

2. Monthly expense tracking
   - Rent
   - Milk
   - Vegetables
   - Travel
   - Utilities and recurring expenses

3. Notification system
   - Budget alerts
   - Low emergency fund notifications
   - Expense thresholds
   - Investment portfolio changes

4. Event-driven processing
   - Kafka-based messaging for notifications and analytics events

5. Data protection and resilience
   - Daily automated Neo4j backups
   - Dockerized environment
   - Graph-based relationship modeling for financial and lifestyle data

## Architecture summary

- Frontend: React 19 dashboard for portfolio, expenses, and notifications
- Backend: Spring Boot REST APIs with domain services for investment and expense ledger flows
- Notification microservice: dedicated Spring Boot app that consumes Kafka events and stores alert records in Neo4j
- Persistence: Neo4j graph DB for relationships and analytics
- Messaging: Kafka for domain events and notification fan-out
- Backup: scheduled shell script to export graph database snapshots daily

The system follows an event-driven split where the core application handles the ledger and the notification service is responsible for budget alerts and user-facing updates.

## Getting started

### Prerequisites

- Docker Desktop or Docker Engine
- Node.js 20+
- Java 21
- Maven 3.9+

### Run infrastructure

```bash
docker compose up -d
```

This starts:

- Neo4j at http://localhost:7474
- Kafka at localhost:9092
- Kafka UI at http://localhost:8083

### Run backend

```bash
cd backend
./mvnw spring-boot:run
```

### Run notification microservice

```bash
cd notification-service
mvn spring-boot:run
```

### Run frontend

```bash
cd frontend
npm install
npm run dev
```

### Daily backup

```bash
chmod +x backup/backup.sh
./backup/backup.sh
```

The backup script stores Neo4j dumps in the `backup` directory and can be scheduled with cron.

## Suggested next features

- Graph Data Science (GDS) for cash-flow forecasting
- Zero-knowledge field-level encryption for sensitive values
- Multi-user and household data isolation
- AI recommendation engine for spending trends
- Budget planning with category health scoring
- Downloadable reports and export to CSV/XLSX

## Default login / access

This starter project does not include authentication yet. It is designed to be extended with:

- Spring Security + JWT authentication
- MFA and role-based access control
- Personal vault encryption

## Project layout

```text
PersonalDataStoreApplication/
├── README.md
├── ARCHITECTURE.md
├── docker-compose.yml
├── .gitignore
├── backup/
│   └── backup.sh
├── backend/
│   ├── pom.xml
│   └── src/
└── frontend/
    ├── package.json
    ├── vite.config.js
    └── src/
```
