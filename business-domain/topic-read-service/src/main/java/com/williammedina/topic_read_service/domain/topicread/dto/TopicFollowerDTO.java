package com.williammedina.topic_read_service.domain.topicread.dto;

import com.williammedina.topic_read_service.domain.topicread.model.Follower;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Datos del usuario que sigue un tópico")
public record TopicFollowerDTO(
        @Schema(description = "Datos del usuario que sigue el tópico")
        UserDTO user,

        @Schema(description = "Fecha en la que el usuario comenzó a seguir el tópico", example = "2025-07-31T15:00:00")
        LocalDateTime followedAt
) {

    public static TopicFollowerDTO fromModel(Follower follower) {

        return new TopicFollowerDTO(
                UserDTO.fromModel(follower.getUser()),
                follower.getFollowedAt()
        );
    }
    public static Follower fromDto(TopicFollowerDTO dto) {

        return new Follower(
                UserDTO.fromDto(dto.user),
                dto.followedAt
        );
    }
}
