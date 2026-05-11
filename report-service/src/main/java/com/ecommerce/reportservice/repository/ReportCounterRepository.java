package com.ecommerce.reportservice.repository;

import com.ecommerce.reportservice.domain.ReportCounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportCounterRepository extends JpaRepository<ReportCounterEntity, Long> {
}

