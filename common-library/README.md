
# Common Library

## Purpose

`common-library` contains shared contracts and utilities used by all services.

## Responsibilities

- Shared API response wrappers
- Shared DTOs for domain and integration payloads
- Shared exception hierarchy
- Shared event envelope models
- Shared utility helpers

## Main Artifacts

- DTOs: `OrderDto`, `PaymentDto`, `ProductDto`, `UserDto`, etc.
- API wrappers: `ApiResponse`, `ErrorResponse`
- Exceptions: `DomainException`, `NotFoundException`, `ValidationException`, etc.
- Events: `EventEnvelope`

## Database Usage

- None

## External Dependencies

- None at runtime (library dependency only)

## Events Published/Consumed

- None (model definitions only)

## Run Locally (module tests)

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl common-library test
```

## Important Config Properties

- No service runtime properties (shared library module)

