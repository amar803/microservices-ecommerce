# Phase 1 Implementation Backlog

This document contains copy-ready GitHub issue templates for Phase 1 (Foundation and Standards).

## Label Set (Suggested)

- `phase:1`
- `type:enhancement`
- `type:docs`
- `area:common-library`
- `area:gateway`
- `area:discovery`
- `area:platform`
- `area:db`
- `priority:P0`
- `priority:P1`

---

## Issue 1 - Normalize configuration profiles across all services

- **Title**: `foundation: normalize configuration profiles across all services`
- **Labels**: `phase:1`, `type:enhancement`, `priority:P0`
- **Estimate**: `L`
- **Assignee**: `@<assignee>`
- **Depends on**: none

### Scope

Add profile-based configuration for each module:

- `src/main/resources/application.yml`
- `src/main/resources/application-local.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-prod.yml`

Modules:

- `api-gateway`
- `discovery-service`
- `auth-service`
- `user-service`
- `product-service`
- `inventory-service`
- `order-service`
- `payment-service`
- `notification-service`
- `report-service`
- `analytics-service`

### Implementation Notes

- Keep existing behavior in `local` profile.
- Use env placeholders in `dev`/`prod` (no hardcoded localhost).
- Keep business logic unchanged.

### Acceptance Criteria

- All services boot with local profile.
- No localhost hardcoding in non-local profiles.
- Startup docs updated with profile usage.

---

## Issue 2 - Common error model and exception hierarchy

- **Title**: `common-library: add enterprise error model and exception hierarchy`
- **Labels**: `phase:1`, `type:enhancement`, `area:common-library`, `priority:P0`
- **Estimate**: `M`
- **Assignee**: `@<assignee>`
- **Depends on**: none

### Scope

In `common-library`, add:

- `error/ErrorCode.java`
- `error/GlobalExceptionDto.java`
- `exception/BusinessException.java`
- `exception/DownstreamServiceException.java`

Align existing exceptions:

- `NotFoundException`
- `ConflictException`

### Acceptance Criteria

- Structured error contract available across services.
- Existing handlers compile without behavior break.
- Unit tests added for DTO serialization and mapping.

---

## Issue 3 - Event envelope and Kafka topic constants

- **Title**: `common-library: standardize EventEnvelope and kafka topic constants`
- **Labels**: `phase:1`, `type:enhancement`, `area:common-library`, `priority:P0`
- **Estimate**: `S`
- **Assignee**: `@<assignee>`
- **Depends on**: Issue 2 (recommended)

### Scope

- Validate and extend `event/EventEnvelope<T>` metadata.
- Add `messaging/KafkaTopics.java` constants only.
- Remove magic topic strings from touched services in this phase.

### Acceptance Criteria

- Topic constants are centralized.
- Event envelope supports correlation metadata.
- Serialization tests pass.

---

## Issue 4 - Correlation ID utilities in common library

- **Title**: `common-library: add correlation id utilities and context helpers`
- **Labels**: `phase:1`, `type:enhancement`, `area:common-library`, `priority:P0`
- **Estimate**: `S`
- **Assignee**: `@<assignee>`
- **Depends on**: none

### Scope

Add shared tracing helpers:

- `tracing/CorrelationIdConstants.java`
- `tracing/CorrelationIdGenerator.java`
- `tracing/CorrelationContext.java`

### Acceptance Criteria

- Header key standardized as `X-Correlation-Id`.
- Utility supports set/get/clear context.
- Unit tests pass.

---

## Issue 5 - Gateway correlation filter and propagation

- **Title**: `gateway: add correlation-id global filter and propagation`
- **Labels**: `phase:1`, `type:enhancement`, `area:gateway`, `priority:P0`
- **Estimate**: `M`
- **Assignee**: `@<assignee>`
- **Depends on**: Issue 4

### Scope

In `api-gateway`:

- Add `GlobalFilter` to read or generate `X-Correlation-Id`.
- Forward header to downstream calls.
- Add same header to gateway response.

### Acceptance Criteria

- Every response includes `X-Correlation-Id`.
- Downstream service sees same value.
- No routing regressions.

---

## Issue 6 - Gateway JWT validation and route security

- **Title**: `gateway: add security baseline (JWT + route-level auth rules)`
- **Labels**: `phase:1`, `type:enhancement`, `area:gateway`, `priority:P0`
- **Estimate**: `M`
- **Assignee**: `@<assignee>`
- **Depends on**: Issue 1

### Scope

- Configure gateway resource-server JWT validation.
- Define public and protected route groups.
- Keep auth policy minimal for Phase 1.

### Acceptance Criteria

- Protected routes reject missing/invalid tokens.
- Public routes remain accessible.
- Route policy documented.

---

## Issue 7 - Gateway request/response logging and fallback standardization

- **Title**: `gateway: add structured logging and standardized fallback payloads`
- **Labels**: `phase:1`, `type:enhancement`, `area:gateway`, `priority:P1`
- **Estimate**: `M`
- **Assignee**: `@<assignee>`
- **Depends on**: Issue 5

### Scope

- Add structured logging filter (method/path/status/latency/correlationId).
- Standardize fallback JSON payloads.
- Ensure sensitive headers are not logged.

### Acceptance Criteria

- Logs include correlation ID and latency.
- Fallback responses share consistent schema.
- Security review confirms no token leakage in logs.

---

## Issue 8 - Gateway Redis rate limiting

- **Title**: `gateway: enable Redis-backed rate limiting`
- **Labels**: `phase:1`, `type:enhancement`, `area:gateway`, `priority:P1`
- **Estimate**: `M`
- **Assignee**: `@<assignee>`
- **Depends on**: Issue 1

### Scope

- Configure Redis rate limiting in gateway routes.
- Externalize limits via env/profile.
- Document default quotas.

### Acceptance Criteria

- Excess requests return `429`.
- Limits are configurable without code changes.
- Local validation steps documented.

---

## Issue 9 - Discovery service profile and health hardening

- **Title**: `discovery-service: profile cleanup + readiness/liveness hardening`
- **Labels**: `phase:1`, `type:enhancement`, `area:discovery`, `priority:P1`
- **Estimate**: `S`
- **Assignee**: `@<assignee>`
- **Depends on**: Issue 1

### Scope

- Add profile-specific config files.
- Keep self-registration disabled where required.
- Add readiness/liveness health exposure.
- Support Docker hostname/env overrides.

### Acceptance Criteria

- Discovery starts cleanly in local and dockerized environments.
- Health probes respond consistently.
- No accidental self-client registration behavior.

---

## Issue 10 - Flyway baseline migrations for DB-backed services

- **Title**: `platform: add Flyway baseline migrations for db-backed services`
- **Labels**: `phase:1`, `type:enhancement`, `area:platform`, `area:db`, `priority:P0`
- **Estimate**: `L`
- **Assignee**: `@<assignee>`
- **Depends on**: Issue 1

### Scope

Add baseline migration folder and `V1__baseline.sql` for:

- `user-service`
- `order-service`
- `payment-service`
- `inventory-service`
- `notification-service`
- `report-service`

### Acceptance Criteria

- Flyway enabled for DB-backed services.
- Fresh database boots with successful migrations.
- Existing local startup remains functional.

---

## Issue 11 - Documentation updates for Phase 1 changes

- **Title**: `docs: update startup and architecture docs for Phase 1`
- **Labels**: `phase:1`, `type:docs`, `priority:P1`
- **Estimate**: `S`
- **Assignee**: `@<assignee>`
- **Depends on**: Issues 1, 5, 6, 8, 10

### Scope

Update:

- `README.md`
- `docs/STARTUP_GUIDE.md`
- `docs/ARCHITECTURE.md`

Include:

- Profile usage and environment variables
- Correlation ID behavior
- Security baseline and route policy
- Rate limiting notes
- Flyway migration notes

### Acceptance Criteria

- A new developer can run services using docs only.
- Phase 1 config/security additions are documented.

---

## Recommended Execution Order

1. Issue 2, 3, 4 (`common-library` contracts)
2. Issue 1 (profiles and config normalization)
3. Issue 5, 6, 7, 8 (`api-gateway` hardening)
4. Issue 9 (`discovery-service` hardening)
5. Issue 10 (Flyway baseline)
6. Issue 11 (docs finalization)

---

## Phase 1 Definition of Done

- Services compile and run with `local` profile.
- Gateway enforces JWT on protected routes.
- Correlation ID is propagated end-to-end.
- Redis rate limiting works with configurable limits.
- DB-backed services use Flyway baseline migrations.
- Docs are updated and runnable.

