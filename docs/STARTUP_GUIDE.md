# Startup Guide

## 1. Prerequisites

- Java 21
- Maven 3.9+
- Docker Desktop
- Git

## 2. Environment Variables

This repo currently uses defaults in `application.yml` and `docker-compose.yml`.

If you need overrides, set these before startup (optional):

- `SPRING_PROFILES_ACTIVE` (to be verified)
- `EUREKA_SERVER_URL` (to be verified)
- `KAFKA_BOOTSTRAP_SERVERS` (to be verified)
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (to be verified)

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

## 8. Troubleshooting

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

### Auth service JWT issuer errors

- Confirm Keycloak is running at `http://localhost:8081`.
- Ensure realm `ecommerce` exists (to be verified).

### Gateway returns 404 on `/`

- This is expected unless a root route is mapped.
- Use API paths such as `/api/v1/*`.

### Service not in Eureka

- Start `discovery-service` first.
- Check service logs for Eureka registration errors.

