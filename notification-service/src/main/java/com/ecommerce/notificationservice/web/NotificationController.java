package com.ecommerce.notificationservice.web;

import com.ecommerce.common.api.ApiResponse;
import com.ecommerce.common.dto.NotificationRequestDto;
import com.ecommerce.notificationservice.service.NotificationService;
import com.ecommerce.notificationservice.service.NotificationService.NotificationRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<NotificationRecord>> send(@RequestBody NotificationRequestDto request) {
        NotificationRecord record = notificationService.send(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(record));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationRecord>>> listRecent() {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.recent()));
    }
}

