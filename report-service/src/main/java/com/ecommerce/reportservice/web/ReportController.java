package com.ecommerce.reportservice.web;

import com.ecommerce.common.api.ApiResponse;
import com.ecommerce.reportservice.service.ReportService;
import com.ecommerce.reportservice.service.ReportService.ReportSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/events")
    public ResponseEntity<ApiResponse<ReportSummary>> recordEvent(@RequestBody RecordEventRequest request) {
        reportService.recordEvent(request.eventType());
        return ResponseEntity.ok(ApiResponse.ok(reportService.getSummary()));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<ReportSummary>> getSummary() {
        return ResponseEntity.ok(ApiResponse.ok(reportService.getSummary()));
    }
}

