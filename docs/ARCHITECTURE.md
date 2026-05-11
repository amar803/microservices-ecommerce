# Architecture

## 1. High-Level Architecture

This platform uses a microservices architecture with:

- API Gateway for routing
- Eureka for service discovery
- Domain-specific services for business capabilities
- Kafka for asynchronous event propagation
- Polyglot persistence (Postgres, Elasticsearch, ClickHouse)

## 2. Request Flow

1. Client sends request to `api-gateway`.
2. Gateway resolves target service through Eureka (`lb://service-name`).
3. Gateway enforces JWT security and applies route-level rate limiting.
4. Target service processes request and returns standardized API response.
5. For orchestrated flows (orders), service-to-service calls and event publishing occur.

## 3. Order Flow (Current MVP)

1. `order-service` creates order record (`CREATED`).
2. Calls `inventory-service` to reserve stock.
3. Calls `payment-service` to authorize/capture payment.
4. Updates order status (`PAID` on success).
5. Publishes `orders.events` Kafka message.
6. Calls `notification-service` and `report-service` APIs.
7. On failure, attempts compensation (`release inventory`) and marks order `FAILED`.

## 4. Service Communication

### Synchronous (REST)

- Client -> Gateway -> Service
- Order orchestration calls:
  - `order-service` -> `inventory-service`
  - `order-service` -> `payment-service`
  - `order-service` -> `notification-service`
  - `order-service` -> `report-service`

### Asynchronous (Kafka)

- `order-service` publishes to `orders.events`
- `notification-service` consumes events
- `report-service` consumes events
- `payments.events` consumption exists in notification/report (producer path to be verified)

## 5. Kafka Usage

- Broker: `localhost:9092` (local)
- Current topics in code:
  - `orders.events`
  - `payments.events` (consumer wiring present)

## 6. Database Strategy

- **Postgres** for transactional domain data (users, orders, payments, inventory, notifications, reports)
- **Elasticsearch** for product search use cases
- **ClickHouse** for analytics workload
- Service-owned schemas/tables via per-service JPA entities

## 7. Security Overview

- `auth-service` uses Spring Security Resource Server
- JWT introspection path backed by issuer metadata (Keycloak realm issuer)
- `api-gateway` enforces JWT validation as resource server
- Public routes: `/api/v1/auth/**`, `/actuator/**`, `/fallback/**`
- `/internal/**` routes require roles `ADMIN` or `SUPPORT`
- All other gateway routes require authentication
- CORS currently permissive for development (`*`)

## 8. Rate Limiting and Traffic Controls

- Redis-backed gateway `RequestRateLimiter` is enabled on public API routes.
- Rate limits are controlled via environment variables:
  - `GATEWAY_RATELIMIT_REPLENISH_RATE`
  - `GATEWAY_RATELIMIT_BURST_CAPACITY`
  - `GATEWAY_RATELIMIT_REQUESTED_TOKENS`
- Correlation IDs are propagated through gateway using `X-Correlation-Id`.

## 9. Observability Overview

- Actuator endpoints enabled across modules
- Prometheus endpoint exposed where configured
- Infrastructure folders include Prometheus/Grafana/ELK assets

Operational visibility updates:

- Gateway request/response logging includes method, path, status, latency, and correlation ID.
- Fallback payloads include structured details (`code`, `service`, `path`, `timestamp`, `correlationId`).

## 10. Data Migration Strategy (Flyway)

- Flyway baseline migrations are enabled for DB-backed services:
  - `user-service`
  - `order-service`
  - `payment-service`
  - `inventory-service`
  - `notification-service`
  - `report-service`
- Each service owns its migration scripts under `db/migration`.
- Each service uses a dedicated Flyway metadata table to avoid conflicts in shared Postgres.

## 11. Current Limitations

- Some modules are still MVP-level in depth/features
- Event schema governance/versioning is minimal
- Full K8s manifests for all services are pending
- Global API documentation (Swagger aggregation) is not finalized

## 12. Related Docs

- `docs/STARTUP_GUIDE.md`
- `docs/diagrams/system-context.md`
- `docs/diagrams/service-architecture.md`
- `docs/diagrams/order-flow.md`
- `docs/diagrams/deployment-flow.md`
- `SOLUTION_ARCHITECTURE.md`
- `FLOWS.md`

