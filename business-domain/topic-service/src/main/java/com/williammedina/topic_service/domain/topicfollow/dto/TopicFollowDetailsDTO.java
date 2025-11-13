package com.williammedina.topic_service.domain.topicfollow.dto;

import com.williammedina.topic_service.domain.topic.dto.TopicDTO;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Detalle del seguimiento a un tópico")
public record TopicFollowDetailsDTO(
        @Schema(description = "Información del tópico seguido")
        TopicDTO topic,

        @Schema(description = "Fecha en la que se siguió el tópico", example = "2025-07-31T15:00:00")
        LocalDateTime followedAt
) {
}
