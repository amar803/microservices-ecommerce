# Discovery Service

## Purpose

`discovery-service` is the Eureka registry used by all microservices for service discovery.

## Responsibilities

- Maintain service instance registry
- Serve discovery metadata to clients (gateway/services)
- Support health-aware registration/lookup

## Main Endpoints

- Eureka dashboard: `/`
- Eureka API: `/eureka/**`
- Health: `/actuator/health`

## Database Usage

- None

## External Dependencies

- None required for startup

## Events Published/Consumed

- None

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl discovery-service spring-boot:run
```

## Important Config Properties

- `server.port=8761`
- `eureka.client.register-with-eureka=false`
- `eureka.client.fetch-registry=false`
- `eureka.server.enable-self-preservation=false` (dev setting)

