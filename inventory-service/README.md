# Inventory Service

## Purpose

`inventory-service` manages stock levels and reservation lifecycle for ordered items.

## Responsibilities

- Upsert inventory item
- Query stock by product or SKU
- Reserve and release inventory

## Main APIs

- `POST /api/v1/inventory/items`
- `GET /api/v1/inventory/items/product/{productId}`
- `GET /api/v1/inventory/items/sku/{sku}`
- `POST /api/v1/inventory/reserve`
- `POST /api/v1/inventory/release`

## Database Usage

- Postgres (`inventory_items` table via JPA)

## External Dependencies

- Postgres
- Eureka

## Events Published/Consumed

- To be verified

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl inventory-service spring-boot:run
```

## Important Config Properties

- `server.port=8087`
- `spring.datasource.*`
- `spring.jpa.hibernate.ddl-auto`
- `eureka.client.service-url.defaultZone`

