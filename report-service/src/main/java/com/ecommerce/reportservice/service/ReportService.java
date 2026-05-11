package com.ecommerce.reportservice.service;

import com.ecommerce.reportservice.domain.ReportCounterEntity;
import com.ecommerce.reportservice.repository.ReportCounterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ReportService {

    private static final Long REPORT_ID = 1L;

    private final ReportCounterRepository reportCounterRepository;

    public ReportService(ReportCounterRepository reportCounterRepository) {
        this.reportCounterRepository = reportCounterRepository;
    }

    @Transactional
    public void recordEvent(String eventType) {
        ReportCounterEntity counter = getOrCreateCounter();

        if (eventType == null) {
            return;
        }

        String normalized = eventType.trim().toUpperCase();
        if (normalized.contains("ORDER")) {
            counter.setTotalOrders(counter.getTotalOrders() + 1);
        }
        if (normalized.contains("PAYMENT") && normalized.contains("CAPTURE")) {
            counter.setTotalPaymentsCaptured(counter.getTotalPaymentsCaptured() + 1);
        }
        if (normalized.contains("INVENTORY") && normalized.contains("RESERV")) {
            counter.setInventoryReservations(counter.getInventoryReservations() + 1);
        }

        reportCounterRepository.save(counter);
    }

    @Transactional
    public ReportSummary getSummary() {
        ReportCounterEntity counter = getOrCreateCounter();
        return new ReportSummary(
                counter.getTotalOrders(),
                counter.getTotalPaymentsCaptured(),
                counter.getInventoryReservations(),
                counter.getUpdatedAt() == null ? Instant.now() : counter.getUpdatedAt()
        );
    }

    private ReportCounterEntity getOrCreateCounter() {
        return reportCounterRepository.findById(REPORT_ID)
                .orElseGet(() -> {
                    ReportCounterEntity entity = new ReportCounterEntity();
                    entity.setId(REPORT_ID);
                    entity.setTotalOrders(0);
                    entity.setTotalPaymentsCaptured(0);
                    entity.setInventoryReservations(0);
                    return reportCounterRepository.save(entity);
                });
    }

    public record ReportSummary(
            long totalOrders,
            long totalPaymentsCaptured,
            long inventoryReservations,
            Instant updatedAt
    ) {
    }
}

