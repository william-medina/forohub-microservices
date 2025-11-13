package com.williammedina.topic_read_service.domain.topicread.dto;

import com.williammedina.topic_read_service.domain.topicread.document.TopicReadDocument;
import com.williammedina.topic_read_service.domain.topicread.model.Follower;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;

@Schema(description = "Detalle del seguimiento a un tópico")
public record TopicFollowDetailsDTO(
        @Schema(description = "Información del tópico seguido")
        TopicDTO topic,

        @Schema(description = "Fecha en la que se siguió el tópico", example = "2025-07-31T15:00:00")
        LocalDateTime followedAt
) {

    public static TopicFollowDetailsDTO fromTopicReadDocument(Long userId, TopicReadDocument topic) {
        return new TopicFollowDetailsDTO(
                TopicDTO.fromDocument(topic),
                Objects.requireNonNull(topic.getFollowers().stream()
                        .filter(f -> f.getUser().getId().equals(userId))
                        .findFirst()
                        .orElse(null)).getFollowedAt()
        );
    }
}
