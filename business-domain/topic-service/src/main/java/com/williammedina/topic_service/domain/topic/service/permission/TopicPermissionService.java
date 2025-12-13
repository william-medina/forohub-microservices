package com.williammedina.topic_service.domain.topic.service.permission;

import com.williammedina.topic_service.domain.topic.dto.UserDTO;
import com.williammedina.topic_service.domain.topic.entity.TopicEntity;

public interface TopicPermissionService {

    UserDTO getCurrentUser(Long currentUserId);
    UserDTO checkCanModify(Long userId, TopicEntity topic);

}
