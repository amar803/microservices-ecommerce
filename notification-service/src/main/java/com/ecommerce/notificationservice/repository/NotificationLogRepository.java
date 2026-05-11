package com.ecommerce.notificationservice.repository;

import com.ecommerce.notificationservice.domain.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long> {

    List<NotificationLogEntity> findTop50ByOrderByCreatedAtDesc();
}

