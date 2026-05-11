# Notification Service

## Purpose

`notification-service` handles notification dispatch APIs and stores notification/event logs.

## Responsibilities

- Send notification requests (current channel support includes email payloads)
- Return recent notification history
- Consume event topics and persist event logs

## Main APIs

- `POST /api/v1/notifications`
- `GET /api/v1/notifications`

## Database Usage

- Postgres (`notification_logs` table via JPA)

## External Dependencies

- Postgres
- Kafka
- SMTP endpoint (`spring.mail.*`, local MailHog recommended)
- Eureka

## Events Published/Consumed

- Consumes topic: `orders.events`
- Consumes topic: `payments.events`
- Publishes: to be verified

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl notification-service spring-boot:run
```

## Important Config Properties

- `server.port=8088`
- `spring.datasource.*`
- `spring.kafka.bootstrap-servers`
- `spring.mail.host`
- `spring.mail.port`
- `eureka.client.service-url.defaultZone`

