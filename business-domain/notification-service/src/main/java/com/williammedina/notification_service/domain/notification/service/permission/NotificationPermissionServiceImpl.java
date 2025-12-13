package com.williammedina.notification_service.domain.notification.service.permission;

import com.williammedina.notification_service.domain.notification.dto.UserDTO;
import com.williammedina.notification_service.domain.notification.entity.NotificationEntity;
import com.williammedina.notification_service.infrastructure.client.UserServiceClient;
import com.williammedina.notification_service.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPermissionServiceImpl implements NotificationPermissionService{

    private final UserServiceClient userServiceClient;

    @Override
    public UserDTO checkCanModify(NotificationEntity notification,Long userId) {
        UserDTO user = userServiceClient.getUserById(userId);
        if (!notification.getUserId().equals(user.id())) {
            log.warn("User ID: {} attempted to modify a notification that does not belong to them (ID: {})", user.id(), notification.getId());
            throw new AppException("No tienes permiso para realizar cambios en esta notificación", HttpStatus.FORBIDDEN);
        }
        return user;
    }
}
