# User Service

## Purpose

`user-service` manages user profile creation and lookup APIs.

## Responsibilities

- Create users
- Read user by ID
- Enforce email uniqueness

## Main APIs

- `POST /api/v1/users`
- `GET /api/v1/users/{userId}`

## Database Usage

- Postgres (`users` table via JPA)

## External Dependencies

- Postgres
- Redis (configured)
- Kafka (configured)
- Eureka

## Events Published/Consumed

- To be verified

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl user-service spring-boot:run
```

## Important Config Properties

- `server.port=8083`
- `spring.datasource.*`
- `spring.data.redis.*`
- `spring.kafka.bootstrap-servers`
- `eureka.client.service-url.defaultZone`

