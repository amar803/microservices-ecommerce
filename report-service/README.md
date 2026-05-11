# Report Service

## Purpose

`report-service` maintains and exposes reporting counters and summary views.

## Responsibilities

- Record report events
- Return summary metrics
- Consume asynchronous events for counter updates

## Main APIs

- `POST /api/v1/reports/events`
- `GET /api/v1/reports/summary`

## Database Usage

- Postgres (`report_counters` table via JPA)

## External Dependencies

- Postgres
- Kafka
- Eureka

## Events Published/Consumed

- Consumes topic: `orders.events`
- Consumes topic: `payments.events`
- Publishes: none

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl report-service spring-boot:run
```

## Important Config Properties

- `server.port=8090`
- `spring.datasource.*`
- `spring.kafka.bootstrap-servers`
- `eureka.client.service-url.defaultZone`

