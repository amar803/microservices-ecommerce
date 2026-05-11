package com.ecommerce.reportservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "report_counters")
public class ReportCounterEntity {

    @Id
    private Long id;

    @Column(nullable = false)
    private long totalOrders;

    @Column(nullable = false)
    private long totalPaymentsCaptured;

    @Column(nullable = false)
    private long inventoryReservations;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    void touch() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalPaymentsCaptured() {
        return totalPaymentsCaptured;
    }

    public void setTotalPaymentsCaptured(long totalPaymentsCaptured) {
        this.totalPaymentsCaptured = totalPaymentsCaptured;
    }

    public long getInventoryReservations() {
        return inventoryReservations;
    }

    public void setInventoryReservations(long inventoryReservations) {
        this.inventoryReservations = inventoryReservations;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

