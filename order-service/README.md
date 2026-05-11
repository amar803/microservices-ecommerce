# Order Service

## Purpose

`order-service` manages order creation and retrieval and orchestrates cross-service order fulfillment steps.

## Responsibilities

- Create order and calculate totals
- Retrieve order by ID
- Orchestrate inventory reservation, payment capture, notification, and reporting
- Publish order events to Kafka

## Main APIs

- `POST /api/v1/orders`
- `GET /api/v1/orders/{orderId}`

## Database Usage

- Postgres (`orders` table via JPA)

## External Dependencies

- Postgres
- Kafka
- Eureka
- Internal REST calls to:
  - `inventory-service`
  - `payment-service`
  - `notification-service`
  - `report-service`

## Events Published/Consumed

- Publishes to topic: `orders.events`
- Consumed events: to be verified

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl order-service spring-boot:run
```

## Important Config Properties

- `server.port=8085`
- `spring.datasource.*`
- `spring.kafka.bootstrap-servers`
- `eureka.client.service-url.defaultZone`

