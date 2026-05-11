# Microservices Ecommerce - Local Runbook

This repository contains Spring Boot microservices with Eureka discovery, API gateway routing, and supporting infrastructure via Docker.

## Documentation

- Solution architecture and class diagrams: `SOLUTION_ARCHITECTURE.md`
- Detailed sequence diagrams and end-to-end method flows: `FLOWS.md`

## Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop

## Infrastructure

Start shared dependencies from the repository root:

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
docker compose up -d
docker compose ps
```

## Build

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn clean install -DskipTests
```

## Startup Order

Start each service in its own terminal.

1. `discovery-service`
2. `auth-service`
3. `user-service`
4. `product-service`
5. `inventory-service`
6. `payment-service`
7. `notification-service`
8. `report-service`
9. `order-service`
10. `analytics-service`
11. `api-gateway`

Example commands:

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl discovery-service spring-boot:run
mvn -pl auth-service spring-boot:run
mvn -pl user-service spring-boot:run
mvn -pl product-service spring-boot:run
mvn -pl inventory-service spring-boot:run
mvn -pl payment-service spring-boot:run
mvn -pl notification-service spring-boot:run
mvn -pl report-service spring-boot:run
mvn -pl order-service spring-boot:run
mvn -pl analytics-service spring-boot:run
mvn -pl api-gateway spring-boot:run
```

## Quick Verification

- Eureka: `http://localhost:8761`
- Gateway health: `http://localhost:8080/actuator/health`
- Auth introspection endpoint: `POST http://localhost:8080/api/v1/auth/introspect`
- Order API: `POST http://localhost:8080/api/v1/orders`

## End-to-End Smoke Flow

Seed inventory:

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/inventory/items" -ContentType "application/json" -Body '{"productId":101,"sku":"SKU-101","availableQuantity":25}'
```

Create order (this triggers inventory reservation, payment authorization/capture, notification, and report update):

```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/orders" -ContentType "application/json" -Body '{"userId":1,"items":[{"productId":101,"sku":"SKU-101","quantity":2,"unitPrice":49.99}]}'
```

Check report summary:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/reports/summary"
```

