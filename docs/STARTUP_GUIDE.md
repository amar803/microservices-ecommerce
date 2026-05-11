# Startup Guide

## 1. Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop
- Git

## 2. Environment Variables

This repo currently uses defaults in `application.yml` and `docker-compose.yml`.

Profile behavior:

- Default profile is `local` for all services.
- Set `SPRING_PROFILES_ACTIVE=dev` or `SPRING_PROFILES_ACTIVE=prod` to switch environments.

If you need overrides, set these before startup (optional):

- `SPRING_PROFILES_ACTIVE`
- `EUREKA_DEFAULT_ZONE`
- `KAFKA_BOOTSTRAP_SERVERS`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `KEYCLOAK_ISSUER_URI`
- `REDIS_HOST`, `REDIS_PORT`
- `GATEWAY_RATELIMIT_REPLENISH_RATE`, `GATEWAY_RATELIMIT_BURST_CAPACITY`, `GATEWAY_RATELIMIT_REQUESTED_TOKENS`

## 3. Start Infrastructure

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
docker compose up -d
docker compose ps
```

## 4. Build Project

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn clean install -DskipTests
```

## 5. Manual Service Startup Order

Start each module in a separate terminal:

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl discovery-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl auth-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl user-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl product-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl inventory-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl payment-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl notification-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl report-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl order-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl analytics-service spring-boot:run -Dspring-boot.run.profiles=local
mvn -pl api-gateway spring-boot:run -Dspring-boot.run.profiles=local
```

## 6. Common URLs

- API Gateway: `http://localhost:8080`
- Eureka: `http://localhost:8761`
- Keycloak: `http://localhost:8081`
- Elasticsearch: `http://localhost:9200`
- Kafka broker: `localhost:9092`
- Postgres: `localhost:5432`
- Redis: `localhost:6379`
- ClickHouse HTTP: `http://localhost:8123`

## 7. Health Check Endpoints

- Gateway: `http://localhost:8080/actuator/health`
- Auth: `http://localhost:8082/actuator/health`
- User: `http://localhost:8083/actuator/health`
- Product: `http://localhost:8084/actuator/health`
- Order: `http://localhost:8085/actuator/health`
- Payment: `http://localhost:8086/actuator/health`
- Inventory: `http://localhost:8087/actuator/health`
- Notification: `http://localhost:8088/actuator/health`
- Analytics: `http://localhost:8089/actuator/health`
- Report: `http://localhost:8090/actuator/health`

Readiness/liveness examples:

- Discovery liveness: `http://localhost:8761/actuator/health/liveness`
- Discovery readiness: `http://localhost:8761/actuator/health/readiness`

## 8. Flyway Baseline Migrations

Flyway is enabled for these services:

- `user-service`
- `order-service`
- `payment-service`
- `inventory-service`
- `notification-service`
- `report-service`

Each service includes:

- `src/main/resources/db/migration/V1__baseline.sql`

Each service uses a dedicated Flyway metadata table to avoid collisions in a shared DB.

## 9. Troubleshooting

### Docker daemon not available

- Ensure Docker Desktop is running.
- Retry:

```powershell
docker info
docker compose ps
```

### Service fails with connection refused

- Verify dependency containers are up (`docker compose ps`).
- Check port conflicts on local machine.

### Gateway returns 401/403 unexpectedly

- Check JWT issuer configuration (`KEYCLOAK_ISSUER_URI`).
- Verify token is valid and signed by configured issuer.
- `/internal/**` requires `ADMIN` or `SUPPORT` role.

### Gateway rate limit responses (429)

- Redis must be reachable from gateway (`REDIS_HOST`, `REDIS_PORT`).
- Tune limits via:
  - `GATEWAY_RATELIMIT_REPLENISH_RATE`
  - `GATEWAY_RATELIMIT_BURST_CAPACITY`
  - `GATEWAY_RATELIMIT_REQUESTED_TOKENS`

### Flyway migration startup failures

- Check DB credentials and connectivity.
- Ensure baseline table names do not collide.
- Verify migration scripts under `db/migration` are present and valid SQL.

### Auth service JWT issuer errors

- Confirm Keycloak is running at `http://localhost:8081`.
- Ensure realm `ecommerce` exists (to be verified).

### Gateway returns 404 on `/`

- This is expected unless a root route is mapped.
- Use API paths such as `/api/v1/*`.

### Service not in Eureka

- Start `discovery-service` first.
- Check service logs for Eureka registration errors.

