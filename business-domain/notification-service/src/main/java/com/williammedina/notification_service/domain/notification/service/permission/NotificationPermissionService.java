package com.williammedina.notification_service.domain.notification.service.permission;

import com.williammedina.notification_service.domain.notification.dto.UserDTO;
import com.williammedina.notification_service.domain.notification.entity.NotificationEntity;

public interface NotificationPermissionService {

    UserDTO checkCanModify(NotificationEntity notification, Long userId);

}
