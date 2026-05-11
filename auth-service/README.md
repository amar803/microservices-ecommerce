# Auth Service

## Purpose

`auth-service` provides authentication-related API boundaries and token introspection using configured JWT issuer metadata.

## Responsibilities

- Token introspection endpoint
- JWT decode/validation through Spring Security resource server
- Standardized API response/error format

## Main APIs

- `POST /api/v1/auth/introspect`

## Database Usage

- None (uses token issuer metadata)

## External Dependencies

- Keycloak issuer (configured): `http://localhost:8081/realms/ecommerce`
- Eureka discovery

## Events Published/Consumed

- None

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl auth-service spring-boot:run
```

## Important Config Properties

- `server.port=8082`
- `spring.security.oauth2.resourceserver.jwt.issuer-uri`
- `eureka.client.service-url.defaultZone`

