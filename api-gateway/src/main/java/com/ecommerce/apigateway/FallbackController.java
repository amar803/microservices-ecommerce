package com.ecommerce.apigateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private ResponseEntity<Map<String, String>> unavailable(String service) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "503",
                        "error", "Service Unavailable",
                        "message", service + " is currently unavailable. Please try again later."
                ));
    }

    @RequestMapping("/users")
    public ResponseEntity<Map<String, String>> usersFallback() {
        return unavailable("user-service");
    }

    @RequestMapping("/products")
    public ResponseEntity<Map<String, String>> productsFallback() {
        return unavailable("product-service");
    }

    @RequestMapping("/orders")
    public ResponseEntity<Map<String, String>> ordersFallback() {
        return unavailable("order-service");
    }
}

