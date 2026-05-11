package com.ecommerce.authservice.web;

import com.ecommerce.authservice.service.AuthService;
import com.ecommerce.authservice.service.AuthService.TokenIntrospection;
import com.ecommerce.common.api.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/introspect")
    public ResponseEntity<ApiResponse<TokenIntrospection>> introspect(@RequestBody TokenIntrospectionRequest request) {
        String token = request.token() == null ? "" : request.token().trim();
        return ResponseEntity.ok(ApiResponse.ok(authService.introspect(token)));
    }
}

