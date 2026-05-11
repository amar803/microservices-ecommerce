package com.ecommerce.reportservice.service;

import com.ecommerce.reportservice.domain.ReportCounterEntity;
import com.ecommerce.reportservice.repository.ReportCounterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ReportCounterRepository reportCounterRepository;

    private ReportService reportService;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(reportCounterRepository);
    }

    @Test
    void recordEventShouldIncrementOrderCounter() {
        ReportCounterEntity entity = new ReportCounterEntity();
        entity.setId(1L);
        entity.setTotalOrders(3);
        entity.setTotalPaymentsCaptured(1);
        entity.setInventoryReservations(2);

        when(reportCounterRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(reportCounterRepository.save(any(ReportCounterEntity.class))).thenAnswer(invocation -> {
            ReportCounterEntity saved = invocation.getArgument(0);
            if (saved.getUpdatedAt() == null) {
                org.springframework.test.util.ReflectionTestUtils.setField(saved, "updatedAt", Instant.now());
            }
            return saved;
        });

        reportService.recordEvent("ORDER_COMPLETED");

        ReportService.ReportSummary summary = reportService.getSummary();
        assertEquals(4, summary.totalOrders());
        assertEquals(1, summary.totalPaymentsCaptured());
    }
}

