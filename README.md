# Microservices E-Commerce Platform

A modular Spring Boot microservices reference implementation for e-commerce workflows (user, product, inventory, order, payment, notification, analytics, and reporting) with centralized API routing, service discovery, and event-driven integration.

## Architecture Overview

- **API Gateway** (`api-gateway`) is the north-south entry point.
- **Eureka** (`discovery-service`) provides service registry and discovery.
- Core domains are split by business capability:
  - identity/auth, users, products, inventory, orders, payments, notifications, analytics, reporting.
- Communication is hybrid:
  - **Synchronous** REST calls for command/query workflows.
  - **Asynchronous** Kafka events for decoupled consumers.
- Data follows a service-owned model using Postgres, Elasticsearch, and ClickHouse.

For full architecture and sequence diagrams see:
- `docs/ARCHITECTURE.md`
- `docs/diagrams/`
- `FLOWS.md`

## Services and Modules

| Module | Port | Purpose |
|---|---:|---|
| `api-gateway` | 8080 | Routes external requests to backend services |
| `discovery-service` | 8761 | Eureka service registry |
| `auth-service` | 8082 | JWT introspection and auth boundary |
| `user-service` | 8083 | User management APIs |
| `product-service` | 8084 | Product catalog/search APIs |
| `order-service` | 8085 | Order orchestration and lifecycle |
| `payment-service` | 8086 | Payment authorization/capture |
| `inventory-service` | 8087 | Inventory reservation/release |
| `notification-service` | 8088 | Notification APIs and event handling |
| `analytics-service` | 8089 | Analytics ingestion/aggregation (MVP) |
| `report-service` | 8090 | Reporting summary APIs and counters |
| `common-library` | n/a | Shared DTOs, API wrappers, exceptions, events |

## Tech Stack

- Java 21, Maven multi-module build
- Spring Boot 3.x
- Spring Cloud (Gateway, Eureka, LoadBalancer)
- Spring Data JPA, Spring Security, Spring Kafka
- PostgreSQL, Redis, Kafka, Elasticsearch, Keycloak, ClickHouse
- Docker Compose (local infra)

## Prerequisites

- Java **21**
- Maven **3.9+**
- Docker Desktop
- Git

## Local Setup

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn clean install -DskipTests
```

## Start Infrastructure (Docker Compose)

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
docker compose up -d
docker compose ps
```

Infra from `docker-compose.yml`:
- Postgres (`5432`)
- Redis (`6379`)
- Kafka (`9092`)
- Elasticsearch (`9200`)
- Keycloak (`8081`)
- ClickHouse (`8123`, `9000`)

## Manual Service Startup Order

Run each in a separate terminal:

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

## Common URLs

- API Gateway: `http://localhost:8080`
- Eureka: `http://localhost:8761`
- Keycloak: `http://localhost:8081`
- Elasticsearch: `http://localhost:9200`

Health check examples:
- `http://localhost:8080/actuator/health`
- `http://localhost:8083/actuator/health`
- `http://localhost:8085/actuator/health`

Swagger/OpenAPI:
- **To be verified** per module (no global Swagger aggregation currently configured).

## Platform Notes

- **Kafka** is used for service decoupling and event fan-out (orders/payments event topics).
- **Postgres** is the primary transactional store for most services.
- **Redis** is configured for user-service support/cache patterns.
- **Elasticsearch** backs product search use cases.
- **Keycloak** provides identity/issuer for JWT validation.

## Repository Structure

```text
microservices-ecommerce/
  api-gateway/
  discovery-service/
  auth-service/
  user-service/
  product-service/
  inventory-service/
  order-service/
  payment-service/
  notification-service/
  analytics-service/
  report-service/
  common-library/
  infrastructure/
  docs/
  postman/
  docker-compose.yml
  pom.xml
```

## Current Limitations

- Some modules are MVP-level and need deeper production hardening.
- Full Kubernetes manifests are not yet available for every service.
- Event contract/versioning strategy is basic and should be formalized.
- API docs (Swagger/OpenAPI) are not centrally published yet.

## Roadmap

- Add contract testing and stronger integration tests (Testcontainers).
- Harden saga/compensation patterns for cross-service orchestration.
- Expand observability dashboards and alerting baselines.
- Complete K8s manifests for all modules and environment overlays.
- Add API governance (OpenAPI publishing + event schema versioning).

