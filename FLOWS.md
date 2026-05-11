# Microservices Ecommerce — Sequence Diagrams & End-to-End Flows

---

## 1. System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          Infrastructure Layer                            │
│   Eureka :8761  │  Postgres :5432  │  Kafka :9092  │  Redis :6379        │
└─────────────────────────────────────────────────────────────────────────┘
                                   │
┌──────────────────────────────────▼──────────────────────────────────────┐
│                         API Gateway :8080                                │
│        Eureka lb:// routing + Circuit Breaker (Resilience4j)             │
└──────────────────────────────────────────────────────────────────────────┘
     /         |          |           |         |          |         \
  :8082      :8083      :8084       :8085     :8086      :8087      :8088   :8090
auth-svc   user-svc  product-svc  order-svc  payment  inventory  notif   report
```

---

## 2. Service Port Map

| Service               | Port | Database      | Kafka Role |
|-----------------------|------|---------------|------------|
| `discovery-service`   | 8761 | —             | —          |
| `api-gateway`         | 8080 | —             | —          |
| `auth-service`        | 8082 | —             | —          |
| `user-service`        | 8083 | Postgres      | —          |
| `product-service`     | 8084 | Elasticsearch | —          |
| `order-service`       | 8085 | Postgres      | Producer   |
| `payment-service`     | 8086 | Postgres      | —          |
| `inventory-service`   | 8087 | Postgres      | —          |
| `notification-service`| 8088 | Postgres      | Consumer   |
| `analytics-service`   | 8089 | —             | —          |
| `report-service`      | 8090 | Postgres      | Consumer   |
| Keycloak              | 8081 | —             | —          |

---

## 3. Order Status State Machine

```
         ┌─────────┐
         │ CREATED │  ← saved on entry
         └────┬────┘
              │ all inventory reserved
         ┌────▼────┐
         │RESERVED │  ← saved after reserve loop
         └────┬────┘
              │ moving to payment
       ┌──────▼──────┐
       │PAYMENT_PEND │  ← saved before auth call
       └──────┬──────┘
              │ payment CAPTURED
         ┌────▼────┐
         │  PAID   │  ← final success state
         └─────────┘

  Any exception in steps 4–11 triggers compensation:
         ┌────────┐
         │ FAILED │  ← inventory released (best-effort) + report event fired
         └────────┘
```

---

## 4. Full End-to-End: Create Order — Happy Path Sequence Diagram

```
Client       API Gateway    order-service     inventory-service   payment-service   notification-service   report-service     Kafka
  │               │               │                  │                  │                   │                   │               │
  │ POST          │               │                  │                  │                   │                   │               │
  │ /api/v1/orders│               │                  │                  │                   │                   │               │
  │──────────────►│               │                  │                  │                   │                   │               │
  │               │ lb://order-   │                  │                  │                   │                   │               │
  │               │ service route │                  │                  │                   │                   │               │
  │               │──────────────►│                  │                  │                   │                   │               │
  │               │        [1] validate request      │                  │                   │                   │               │
  │               │        [2] calc total amount     │                  │                   │                   │               │
  │               │        [3] save order CREATED    │                  │                   │                   │               │
  │               │               │                  │                  │                   │                   │               │
  │               │        [4] reserveInventory()    │                  │                   │                   │               │
  │               │               │ POST /inventory  │                  │                   │                   │               │
  │               │               │ /reserve         │                  │                   │                   │               │
  │               │               │─────────────────►│                  │                   │                   │               │
  │               │               │         check remaining qty         │                   │                   │               │
  │               │               │         reservedQty += qty         │                   │                   │               │
  │               │               │         Postgres UPDATE            │                   │                   │               │
  │               │               │◄─────────────────│                  │                   │                   │               │
  │               │               │ {reserved: true} │                  │                   │                   │               │
  │               │        [5] save order RESERVED   │                  │                   │                   │               │
  │               │        [6] save order PAYMENT_PENDING              │                   │                   │               │
  │               │               │                  │                  │                   │                   │               │
  │               │        [7] authorizePayment()    │                  │                   │                   │               │
  │               │               │ POST /payments   │                  │                   │                   │               │
  │               │               │──────────────────────────────────►  │                   │                   │               │
  │               │               │                  │    idempotency check                 │                   │               │
  │               │               │                  │    save AUTHORIZED                  │                   │               │
  │               │               │                  │    Postgres INSERT                  │                   │               │
  │               │               │◄─────────────────────────────────── │                   │                   │               │
  │               │               │ {id:501, status: AUTHORIZED}        │                   │                   │               │
  │               │               │                  │                  │                   │                   │               │
  │               │        [8] capturePayment(501)   │                  │                   │                   │               │
  │               │               │ POST /payments   │                  │                   │                   │               │
  │               │               │ /501/capture     │                  │                   │                   │               │
  │               │               │──────────────────────────────────►  │                   │                   │               │
  │               │               │                  │    validate AUTHORIZED              │                   │               │
  │               │               │                  │    status = CAPTURED               │                   │               │
  │               │               │                  │    Postgres UPDATE                 │                   │               │
  │               │               │◄─────────────────────────────────── │                   │                   │               │
  │               │               │ {status: CAPTURED}│                 │                   │                   │               │
  │               │               │                  │                  │                   │                   │               │
  │               │        [9] save order PAID       │                  │                   │                   │               │
  │               │       [10] publishOrderEvent("ORDER_PAID")         │                   │                   │               │
  │               │               │──────────────────────────────────────────────────────────────────────────────────────────►  │
  │               │               │                  │                  │              orders.events topic                      │
  │               │               │                  │                  │                   │                   │               │
  │               │       [11] sendOrderNotification()                 │                   │                   │               │
  │               │               │ POST /notifications                │                   │                   │               │
  │               │               │──────────────────────────────────────────────────────►  │                   │               │
  │               │               │                  │                  │    persist to notification_logs      │               │
  │               │               │◄─────────────────────────────────────────────────────── │                   │               │
  │               │               │                  │                  │                   │                   │               │
  │               │       [12] publishReportEvent("ORDER_COMPLETED")   │                   │                   │               │
  │               │               │ POST /reports/events               │                   │                   │               │
  │               │               │──────────────────────────────────────────────────────────────────────────►  │               │
  │               │               │                  │                  │                   │  increment totalOrders            │
  │               │               │                  │                  │                   │  Postgres UPDATE                  │
  │               │               │◄─────────────────────────────────────────────────────────────────────────── │               │
  │               │               │                  │                  │                   │                   │               │
  │               │◄──────────────│                  │                  │                   │                   │               │
  │ 201 Created   │               │                  │                  │                   │                   │               │
  │ {status:PAID} │               │                  │                  │                   │                   │               │
  │◄──────────────│               │                  │                  │                   │                   │               │
```

---

## 5. Failure & Compensation Flow

```
Client        order-service      inventory-service    payment-service
  │                 │                   │                   │
  │ POST /orders    │                   │                   │
  │────────────────►│                   │                   │
  │                 │ save (CREATED)    │                   │
  │                 │ reserve() ────────►                   │
  │                 │ {reserved:true} ◄─│                   │
  │                 │ save (RESERVED)   │                   │
  │                 │ authorize() ──────────────────────────►
  │                 │                   │         PAYMENT FAILS
  │                 │ ◄── Exception ────────────────────────│
  │                 │                   │                   │
  │      [COMPENSATION]                 │                   │
  │                 │ releaseInventory() (best-effort) ─────►
  │                 │ save (FAILED)     │                   │
  │                 │ publishReportEvent("ORDER_FAILED")    │
  │◄────────────────│                   │                   │
  │ 502 DOWNSTREAM_ORCHESTRATION_ERROR  │                   │
```

---

## 6. Kafka Event Flow

```
order-service ──publish──► orders.events ──consume──► notification-service
                                                           └─ acceptEvent()
                                                           └─ persist log (Postgres)

                         orders.events ──consume──► report-service
                                                       └─ recordEvent("ORDER_EVENT")
                                                       └─ totalOrders++ (Postgres)

                         payments.events (future) ──consume──► notification-service
                                                               └─ onPaymentEvent()

                         payments.events (future) ──consume──► report-service
                                                               └─ totalPaymentsCaptured++
```

---

## 7. Method-Level Call Chain

### `POST /api/v1/orders` — Full Stack Trace

```
[1]  HTTP Client
       → API Gateway :8080
       → Eureka resolves lb://order-service → order-service :8085

[2]  OrderController.createOrder(CreateOrderRequest)
       → @Valid check: userId != null, items non-empty

[3]  OrderService.create(CreateOrderRequest)
       → items.stream() → toOrderItem() → List<OrderItemDto>
       → total = Σ(unitPrice × quantity)
       → new OrderEntity { status = CREATED }
       → orderRepository.save(entity)                 ← Postgres INSERT

[4]  OrderService.reserveInventory(order, items)
       → for each OrderItemDto:
           OrderWorkflowClient.reserveInventory(productId, sku, qty)
             → RestTemplate POST lb://inventory-service/api/v1/inventory/reserve
               → InventoryController.reserve(ReserveInventoryRequest)
                 → InventoryService.reserve()
                   → inventoryRepository.findByProductId()   ← Postgres SELECT
                   → validateSku(entity, sku)
                   → remaining = available - reserved
                   → if remaining < qty → return { reserved: false }
                   → entity.reservedQty += qty
                   → inventoryRepository.save()              ← Postgres UPDATE
                   → return InventoryReservationDto { reserved: true }
       → if any false → throw OrchestrationException (triggers compensation)
       → order.status = RESERVED
       → orderRepository.save()                             ← Postgres UPDATE

[5]  order.status = PAYMENT_PENDING
     orderRepository.save()                                 ← Postgres UPDATE

[6]  OrderWorkflowClient.authorizePayment(orderId, amount, idempotencyKey)
       → RestTemplate POST lb://payment-service/api/v1/payments
         → PaymentController.createPayment(CreatePaymentRequest)
           → PaymentService.create()
             → paymentRepository.findByIdempotencyKey()     ← Postgres SELECT
             → if found → return existing (idempotent)
             → new PaymentEntity { status = AUTHORIZED }
             → providerReference = "prov-" + UUID
             → paymentRepository.save()                     ← Postgres INSERT
             → return PaymentDto { id: 501, status: AUTHORIZED }

[7]  OrderWorkflowClient.capturePayment(paymentId = 501)
       → RestTemplate POST lb://payment-service/api/v1/payments/501/capture
         → PaymentController.capturePayment(501)
           → PaymentService.capture(501)
             → paymentRepository.findById(501)              ← Postgres SELECT
             → validate status == AUTHORIZED
             → entity.status = CAPTURED
             → paymentRepository.save()                     ← Postgres UPDATE
             → return PaymentDto { status: CAPTURED }

[8]  order.status = PAID
     orderRepository.save()                                 ← Postgres UPDATE

[9]  OrderService.publishOrderEvent("ORDER_PAID", orderId, orderDto)
       → EventEnvelope.of("ORDER_PAID", "order-service", correlationId, orderDto)
       → kafkaTemplate.send("orders.events", key = orderId, envelope)   ← Kafka PUBLISH

[10] OrderWorkflowClient.sendOrderNotification(userId, orderId, amount)
       → RestTemplate POST lb://notification-service/api/v1/notifications
         → NotificationController.send(NotificationRequestDto)
           → NotificationService.send()
             → new NotificationLogEntity { channel = EMAIL, subject = "Order confirmed" }
             → notificationLogRepository.save()             ← Postgres INSERT (notification_logs)
             → log.info("Notification sent …")
             → return NotificationRecord

[11] OrderWorkflowClient.publishReportEvent("ORDER_COMPLETED")
       → RestTemplate POST lb://report-service/api/v1/reports/events
         → ReportController.recordEvent(RecordEventRequest)
           → ReportService.recordEvent("ORDER_COMPLETED")
             → reportCounterRepository.findById(1L)         ← Postgres SELECT (report_counters)
             → if not found → seed zero counter
             → counter.totalOrders += 1
             → reportCounterRepository.save()               ← Postgres UPDATE

[12] return OrderDto { id, userId, items, totalAmount, status = PAID }
       → HTTP 201 Created → Client
```

---

## 8. Individual Service API Flows

### User Service

```
POST /api/v1/users
  → UserController.createUser(CreateUserRequest)
    → UserService.create()
      → userRepository.existsByEmailIgnoreCase()    ← Postgres SELECT
      → if exists → throw ConflictException → 409
      → new UserEntity { email, firstName, lastName, active = true }
      → userRepository.save()                       ← Postgres INSERT
      → return UserDto → 201

GET /api/v1/users/{userId}
  → UserController.getUser(userId)
    → UserService.getById()
      → userRepository.findById()                   ← Postgres SELECT
      → if missing → throw NotFoundException → 404
      → return UserDto → 200
```

### Product Service

```
POST /api/v1/products
  → ProductController.createProduct(CreateProductRequest)
    → ProductService.create()
      → new ProductDocument
      → productRepository.save()                    ← Elasticsearch INDEX
      → return ProductDto → 201

GET /api/v1/products/{productId}
  → ProductController.getProduct(productId)
    → ProductService.getById()
      → productRepository.findById()                ← Elasticsearch GET
      → if missing → 404
      → return ProductDto → 200
```

### Inventory Service

```
POST /api/v1/inventory/items
  → InventoryController.upsertItem(CreateInventoryItemRequest)
    → InventoryService.upsert()
      → find by productId or create new
      → set availableQty, sku
      → inventoryRepository.save()                  ← Postgres UPSERT
      → return InventoryItemView → 201

GET /api/v1/inventory/items/product/{productId}
  → InventoryService.getByProductId()
    → inventoryRepository.findByProductId()         ← Postgres SELECT → 200

GET /api/v1/inventory/items/sku/{sku}
  → InventoryService.getBySku()
    → inventoryRepository.findBySkuIgnoreCase()     ← Postgres SELECT → 200

POST /api/v1/inventory/reserve
  → InventoryService.reserve()
    → check qty, increment reservedQty              ← Postgres UPDATE
    → return { reserved: true/false }

POST /api/v1/inventory/release
  → InventoryService.release()
    → decrement reservedQty (min 0)                 ← Postgres UPDATE
    → return updated InventoryItemView
```

### Payment Service

```
POST /api/v1/payments
  → PaymentController.createPayment(CreatePaymentRequest)
    → PaymentService.create()
      → idempotency: findByIdempotencyKey()         ← Postgres SELECT
      → if exists → return same record (deduplication)
      → new PaymentEntity { status = AUTHORIZED }
      → paymentRepository.save()                    ← Postgres INSERT → 201

GET /api/v1/payments/{paymentId}
  → PaymentService.getById()
    → paymentRepository.findById()                  ← Postgres SELECT → 200

POST /api/v1/payments/{paymentId}/capture
  → PaymentService.capture()
    → validate status == AUTHORIZED
    → entity.status = CAPTURED
    → paymentRepository.save()                      ← Postgres UPDATE → 200
```

### Notification Service

```
POST /api/v1/notifications
  → NotificationController.send(NotificationRequestDto)
    → NotificationService.send()
      → new NotificationLogEntity
      → notificationLogRepository.save()            ← Postgres INSERT
      → return NotificationRecord → 201

GET /api/v1/notifications
  → NotificationController.listRecent()
    → notificationLogRepository.findTop50ByOrderByCreatedAtDesc()
    → return List<NotificationRecord> → 200

@KafkaListener(orders.events)
  → NotificationEventListener.onOrderEvent(payload)
    → NotificationService.acceptEvent("orders.events", payload)
      → persist log entry                           ← Postgres INSERT

@KafkaListener(payments.events)
  → NotificationEventListener.onPaymentEvent(payload)
    → NotificationService.acceptEvent("payments.events", payload)
```

### Report Service

```
POST /api/v1/reports/events
  → ReportController.recordEvent(RecordEventRequest)
    → ReportService.recordEvent(eventType)
      → findById(1) or create counter row           ← Postgres SELECT/INSERT
      → if "ORDER" in type → totalOrders++
      → if "PAYMENT_CAPTURE" in type → totalPaymentsCaptured++
      → if "INVENTORY_RESERV" in type → inventoryReservations++
      → reportCounterRepository.save()              ← Postgres UPDATE → 200

GET /api/v1/reports/summary
  → ReportService.getSummary()
    → reportCounterRepository.findById(1)           ← Postgres SELECT
    → return ReportSummary { totalOrders, totalPaymentsCaptured, inventoryReservations } → 200

@KafkaListener(orders.events)
  → ReportEventListener.onOrderEvent()
    → ReportService.recordEvent("ORDER_EVENT")

@KafkaListener(payments.events)
  → ReportEventListener.onPaymentEvent()
    → ReportService.recordEvent("PAYMENT_CAPTURED")
```

### Auth Service

```
POST /api/v1/auth/introspect  { "token": "eyJ..." }
  → AuthController.introspect(TokenIntrospectionRequest)
    → AuthService.introspect(token)
      → jwtDecoder.decode(token)      ← verifies signature against Keycloak issuer-uri
      → extract subject + realm_access.roles
      → return TokenIntrospection { active: true, subject, role, expiresAt }
      → on JwtException → return { active: false }
      → response → 200
```

---

## 9. Gateway Circuit Breaker Behaviour

```
Client → Gateway → (lb://user-service DOWN) → CircuitBreaker OPEN
                                                    │
                                                    ▼
                                  FallbackController.fallback("/fallback/users")
                                  → 503 { "service temporarily unavailable" }

After waitDurationInOpenState (10s):
  CircuitBreaker → HALF_OPEN
  → allow 3 test calls (permittedNumberOfCallsInHalfOpenState)
  → if success rate > threshold → CLOSED again
```

Circuit breaker config (from `api-gateway/src/main/resources/application.yml`):

| Setting | Value |
|---|---|
| `slidingWindowSize` | 10 |
| `failureRateThreshold` | 50% |
| `waitDurationInOpenState` | 10 seconds |
| `permittedNumberOfCallsInHalfOpenState` | 3 |
| `timeoutDuration` | 3 seconds |

Circuit breakers are configured for: `user-service`, `product-service`, `order-service`.

---

## 10. Eureka Load Balancing

```
New service instance starts
        │
        ▼
Register with Eureka :8761
(instanceId, IP, port, health URL)

API Gateway request arrives
        │
        ▼
lb://inventory-service
        │
        ▼
Spring Cloud LoadBalancer asks Eureka:
"Give me all HEALTHY instances of inventory-service"
        │
        ▼
Round-robin across all returned IPs
(auto scales with zero config changes)

Scale out example:
  mvn -pl inventory-service spring-boot:run -Dspring-boot.run.arguments="--server.port=8097"
  ← Eureka picks this up within ~10 seconds
  ← Gateway auto-routes 50% of traffic to new instance
```

---

## 11. End-to-End Smoke Test Commands

```powershell
# Step 1: Create a user
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/users" `
  -ContentType "application/json" `
  -Body '{"email":"amar@example.com","firstName":"Amar","lastName":"J"}'

# Step 2: Create a product
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/products" `
  -ContentType "application/json" `
  -Body '{"name":"Widget Pro","description":"A great widget","price":49.99,"category":"Electronics"}'

# Step 3: Seed inventory for productId=1
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/inventory/items" `
  -ContentType "application/json" `
  -Body '{"productId":1,"sku":"SKU-001","availableQuantity":50}'

# Step 4: Place an order (triggers full orchestration flow)
Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/v1/orders" `
  -ContentType "application/json" `
  -Body '{"userId":1,"items":[{"productId":1,"sku":"SKU-001","quantity":2,"unitPrice":49.99}]}'

# Step 5: Check inventory was reserved
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/inventory/items/product/1"

# Step 6: Check notification was sent
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/notifications"

# Step 7: Check report summary
Invoke-RestMethod -Uri "http://localhost:8080/api/v1/reports/summary"

# Step 8: Eureka dashboard
Start-Process "http://localhost:8761"
```

