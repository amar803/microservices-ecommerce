# Analytics Service

## Purpose

`analytics-service` is intended for analytical/event processing workloads and ClickHouse-based data operations.

## Responsibilities

- Consume analytics-related events (MVP stage)
- Write/read analytical data in ClickHouse (to be expanded)

## Main APIs

- Public business APIs: **to be verified**
- Internal health endpoint via actuator

## Database Usage

- ClickHouse (`jdbc:clickhouse://localhost:8123/default`)

## External Dependencies

- Kafka
- ClickHouse
- Eureka

## Events Published/Consumed

- To be verified

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl analytics-service spring-boot:run
```

## Important Config Properties

- `server.port=8089`
- `spring.kafka.bootstrap-servers`
- `spring.datasource.url` (ClickHouse)
- `eureka.client.service-url.defaultZone`

