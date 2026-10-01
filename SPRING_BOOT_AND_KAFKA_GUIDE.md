# Spring Boot, Kafka, and Frontend Concepts Guide

This document explains the key concepts used in this project.

## 1. How serialization works in Kafka

Kafka stores messages as bytes. The producer sends a message in a serialized format, and the consumer deserializes it back into Java objects or maps.

### Why serialization is needed
Kafka does not understand Java objects directly. It only understands binary data.

So before sending, Java objects must be converted to bytes.

### Common serializers
- `StringSerializer` for strings
- `JsonSerializer` for JSON payloads
- `ByteArraySerializer` for raw bytes

### Example in this project
The backend uses JSON serialization for Kafka messages.

```java
config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
```

This means the event payload is converted into JSON before being sent to Kafka.

On the consumer side:

```java
config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
```

This converts the JSON back to a Java `Map` or object.

### Important idea
Producer:
- Java object/map -> JSON bytes -> Kafka

Consumer:
- Kafka bytes -> JSON -> Java object/map

### Why this matters
Without serialization, Kafka would only be able to store raw bytes, not meaningful application data.

---

## 2. How application.yml is read by the application

Spring Boot automatically loads configuration files from:

- `application.properties`
- `application.yml`
- `application.yaml`

The default location is:
- `src/main/resources`

### Example
This project has:

```yaml
server:
  port: 8082

spring:
  kafka:
    bootstrap-servers: localhost:9092
  neo4j:
    uri: bolt://localhost:7687
```

Spring Boot uses the `SpringEnvironment` and `@ConfigurationProperties` or direct `@Value` injection to read these values.

### Example with @Value

```java
@Value("${spring.kafka.bootstrap-servers}")
private String bootstrapServers;
```

This tells Spring:
- look for the property named `spring.kafka.bootstrap-servers`
- inject its value into the Java field

### How Spring reads it internally
Spring Boot loads all configuration sources, merges them, and exposes them as key-value properties.

Then the application reads them from the environment.

---

## 3. How database connection is provided to the application

In this project, database connection details are configured in `application.yml`.

Example:

```yaml
spring:
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: password123
```

Spring Boot sees these values and uses the Neo4j auto-configuration to build the database connection.

### What happens behind the scenes
Spring Boot auto-configuration:
- creates a `Driver` for Neo4j
- creates the connection factory
- injects repositories like `Neo4jRepository`
- lets the application use `save()`, `findAll()`, etc.

### In this project
The application uses Spring Data Neo4j, so the repository layer automatically connects to the configured database.

Example repository:

```java
public interface AlertNotificationRepository extends Neo4jRepository<AlertNotification, Long> {
}
```

The framework handles the connection and database operations for you.

---

## 4. Difference between .yml and .properties files

Both are configuration files for Spring Boot.

### `.properties` format
```properties
server.port=8082
spring.kafka.bootstrap-servers=localhost:9092
```

### `.yml` format
```yaml
server:
  port: 8082
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

### Main differences

#### 1. Readability
- YAML is more readable and hierarchical
- Properties is flat and simple

#### 2. Structure
- YAML supports nested configuration cleanly
- Properties requires dot-separated keys

#### 3. Ease of maintenance
- YAML is better for large configs
- Properties is useful for simple settings

#### 4. Spring Boot support
Spring Boot supports both equally well.

### Which one should be used?
- Use `application.yml` when configuration is hierarchical and large
- Use `.properties` for small or simple config files

---

## 5. What is @SpringBootApplication

`@SpringBootApplication` is a combination annotation in Spring Boot.

It is usually placed on the main class:

```java
@SpringBootApplication
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
```

### It combines three important annotations

#### 1. `@SpringBootConfiguration`
Marks the class as a Spring Boot configuration source.

#### 2. `@EnableAutoConfiguration`
Tells Spring Boot to automatically configure application components like:
- web server
- database
- security
- Kafka
- Neo4j

#### 3. `@ComponentScan`
Scan for Spring-managed classes like:
- `@Service`
- `@Repository`
- `@Controller`
- `@Configuration`

### Why it matters
It starts the application and wires everything automatically.

---

## 6. What is @Value

`@Value` is used to inject a single configuration value from the environment or property file.

### Example

```java
@Value("${spring.kafka.bootstrap-servers}")
private String bootstrapServers;
```

This means:
- read the property `spring.kafka.bootstrap-servers`
- assign it to the variable `bootstrapServers`

### When to use it
Use `@Value` for a small number of simple values.

### Example use cases
- port numbers
- external service URLs
- API keys
- feature flags

### Better choice for bigger configs
For larger config objects, prefer:

```java
@ConfigurationProperties
```

This is cleaner and more structured.

---

## 7. Domain vs DTO

These are two different layers in application design.

### Domain
Domain classes represent the business model of the application.

Example in this project:
- `Investment`
- `Expense`
- `Notification`

These classes usually map directly to database entities or business entities.

#### Domain characteristics
- contains business meaning
- models real concepts in the app
- may be persisted in Neo4j
- often contains validation and behavior

### DTO (Data Transfer Object)
DTOs are used to carry data between layers, especially between API layer and service layer.

Example:
- `InvestmentRequest`
- `ExpenseRequest`

#### DTO characteristics
- simple data holder
- used for input/output transfer
- can contain only the fields needed for a request
- avoids exposing internal entity structure directly

### Example
Backend service receives a request object:

```java
public record InvestmentRequest(
    String name,
    String type,
    String category,
    double amount,
    double currentValue,
    LocalDate purchaseDate,
    String notes
) {}
```

Then the service maps it into a domain entity:

```java
Investment investment = new Investment(...);
```

### Why this matters
DTOs protect the domain model and help separate API contract from persistence model.

---

## 8. What is Vite frontend

Vite is a modern frontend build tool for React, Vue, and other JavaScript frameworks.

### Why Vite is used
It provides:
- fast local development server
- quick hot reload
- optimized production build
- simple config

### In this project
The frontend is built using React + Vite.

Typical commands:

```bash
npm install
npm run dev
npm run build
```

### Vite responsibilities
- starts the dev server
- bundles the app for development
- builds production files
- optimizes assets

### Example config file
This project uses Vite configuration in the frontend folder.

### Vite vs older tools
Compared with older build tools, Vite is much faster because it uses native ES modules and does less upfront work.

### Frontend architecture in this app
- React renders UI components
- Vite serves the frontend locally
- the browser calls backend APIs
- backend stores data in Neo4j and publishes events to Kafka

---

## Summary

- Kafka serialization converts Java objects into bytes and back using serializers/deserializers.
- `application.yml` is read automatically by Spring Boot via the environment.
- Database connection is configured in `application.yml` and auto-wired by Spring Data Neo4j.
- YAML is more readable and hierarchical than `.properties`.
- `@SpringBootApplication` starts the app and enables auto-configuration and component scanning.
- `@Value` injects one property value directly.
- Domain models represent business data; DTOs are for transport and API contracts.
- Vite is a fast modern frontend build tool used to run and build the React app.
