package com.williammedina.topic_service.domain.topic.service.permission;

import com.williammedina.topic_service.domain.topic.dto.UserDTO;
import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import com.williammedina.topic_service.infrastructure.client.UserServiceClient;
import com.williammedina.topic_service.infrastructure.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TopicPermissionServiceImpl implements TopicPermissionService {

    private final UserServiceClient userServiceClient;

    @Override
    public UserDTO getCurrentUser(Long currentUserId) {
        return userServiceClient.getUserById(currentUserId);
    }

    @Override
    public UserDTO checkCanModify(Long userId, TopicEntity topic) {
        UserDTO user = getCurrentUser(userId);
        if (!topic.getUserId().equals(user.id()) && !user.hasElevatedPermissions()) {
            log.warn("User without permission ID: {} attempted to modify topic ID: {}", user.id(), topic.getId());
            throw new AppException("No tienes permiso para realizar cambios en este tópico", HttpStatus.FORBIDDEN);
        }
        return user;
    }
}
