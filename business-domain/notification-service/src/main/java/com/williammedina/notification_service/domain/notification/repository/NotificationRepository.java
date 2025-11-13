package com.williammedina.notification_service.domain.notification.repository;

import com.williammedina.notification_service.domain.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    List<NotificationEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);

}
