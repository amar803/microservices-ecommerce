# Product Service

## Purpose

`product-service` provides product catalog APIs with search support.

## Responsibilities

- Create products
- Fetch product by SKU
- Search products by name

## Main APIs

- `POST /api/v1/products`
- `GET /api/v1/products/{sku}`
- `GET /api/v1/products/search?q=...`

## Database Usage

- Elasticsearch index-backed storage

## External Dependencies

- Elasticsearch (`http://localhost:9200`)
- Eureka

## Events Published/Consumed

- To be verified

## Run Locally

```powershell
Set-Location "C:\Users\amarj\microservices-ecommerce"
mvn -pl product-service spring-boot:run
```

## Important Config Properties

- `server.port=8084`
- `spring.elasticsearch.uris`
- `eureka.client.service-url.defaultZone`

