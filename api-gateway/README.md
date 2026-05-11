# API Gateway

## Purpose

`api-gateway` is the single entry point for external API traffic. It routes requests to backend services using service discovery (`lb://...`) via Eureka.

## Responsibilities

- Path-based routing to domain services
- Circuit breaker fallback for selected routes
- Internal pass-through routes for operational endpoints
- Global CORS policy (currently permissive for development)

## Main Endpoints (Gateway-facing)

- `/api/v1/auth/**`
- `/api/v1/users/**`
- `/api/v1/products/**`
- `/api/v1/orders/**`
- `/api/v1/inventory/**`
- `/api/v1/payments/**`
- `/api/v1/notifications/**`
- `/api/v1/reports/**`
- `/fallback/*` (fallback controller)

## Database Usage

- None

## External Dependencies

- Eureka (`http://localhost:8761/eureka/`)
- Downstream services registered in Eureka

## Events Published/Consumed

- None

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl api-gateway spring-boot:run
```

## Important Config Properties

- `server.port=8080`
- `spring.cloud.gateway.routes`
- `resilience4j.circuitbreaker.instances.*`
- `eureka.client.service-url.defaultZone`
- `management.endpoints.web.exposure.include`

