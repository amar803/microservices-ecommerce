# Payment Service

## Purpose

`payment-service` provides payment authorization/capture APIs with idempotency support.

## Responsibilities

- Create payment authorization
- Capture authorized payment
- Retrieve payment details

## Main APIs

- `POST /api/v1/payments`
- `GET /api/v1/payments/{paymentId}`
- `POST /api/v1/payments/{paymentId}/capture`

## Database Usage

- Postgres (`payments` table via JPA)

## External Dependencies

- Postgres
- Eureka

## Events Published/Consumed

- To be verified

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl payment-service spring-boot:run
```

## Important Config Properties

- `server.port=8086`
- `spring.datasource.*`
- `spring.jpa.hibernate.ddl-auto`
- `eureka.client.service-url.defaultZone`

