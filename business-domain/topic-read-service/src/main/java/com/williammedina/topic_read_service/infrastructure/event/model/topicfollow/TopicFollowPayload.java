package com.williammedina.topic_read_service.infrastructure.event.model.topicfollow;

import com.williammedina.topic_read_service.domain.topicread.dto.TopicFollowerDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Evento emitido cuando un usuario sigue o deja de seguir un tópico (seguir, dejar de seguir)")
public record TopicFollowPayload(

        @Schema(description = "Información del usuario que provocó el evento")
        TopicFollowerDTO topicFollower,

        @Schema(description = "ID del tópico afectado por el evento")
        Long topicId

) {
}
