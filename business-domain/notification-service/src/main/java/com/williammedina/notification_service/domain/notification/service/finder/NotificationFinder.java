package com.williammedina.notification_service.domain.notification.service.finder;

import com.williammedina.notification_service.domain.notification.entity.NotificationEntity;

public interface NotificationFinder {

    NotificationEntity findNotificationById(Long notifyId);

}
