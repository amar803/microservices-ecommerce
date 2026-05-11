package com.ecommerce.apigateway;


import com.ecommerce.common.tracing.CorrelationIdConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private ResponseEntity<Map<String, Object>> unavailable(String service, ServerHttpRequest request) {
        String correlationId = request.getHeaders().getFirst(CorrelationIdConstants.HEADER_NAME);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", 503,
                        "code", "DOWNSTREAM_UNAVAILABLE",
                        "error", "Service Unavailable",
                        "service", service,
                        "path", request.getPath().value(),
                        "message", service + " is currently unavailable. Please try again later.",
                        "correlationId", correlationId == null ? "N/A" : correlationId,
                        "timestamp", Instant.now().toString()
                ));
    }

    @RequestMapping("/users")
    public ResponseEntity<Map<String, Object>> usersFallback(ServerHttpRequest request) {
        return unavailable("user-service", request);
    }

    @RequestMapping("/products")
    public ResponseEntity<Map<String, Object>> productsFallback(ServerHttpRequest request) {
        return unavailable("product-service", request);
    }

    @RequestMapping("/orders")
    public ResponseEntity<Map<String, Object>> ordersFallback(ServerHttpRequest request) {
        return unavailable("order-service", request);
    }
}

