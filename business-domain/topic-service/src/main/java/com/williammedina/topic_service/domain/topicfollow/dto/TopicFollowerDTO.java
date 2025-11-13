package com.williammedina.topic_service.domain.topicfollow.dto;

import com.williammedina.topic_service.domain.topic.dto.UserDTO;
import com.williammedina.topic_service.domain.topicfollow.entity.TopicFollowEntity;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Datos del usuario que sigue un tópico")
public record TopicFollowerDTO(
        @Schema(description = "Datos del usuario que sigue el tópico")
        UserDTO user,

        @Schema(description = "Fecha en la que el usuario comenzó a seguir el tópico", example = "2025-07-31T15:00:00")
        LocalDateTime followedAt
) {
}
