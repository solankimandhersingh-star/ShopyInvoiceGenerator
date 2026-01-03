package com.shopy.generator.notificationservice.repository;

import com.shopy.generator.notificationservice.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository
        extends JpaRepository<NotificationLog, Long> {
}

