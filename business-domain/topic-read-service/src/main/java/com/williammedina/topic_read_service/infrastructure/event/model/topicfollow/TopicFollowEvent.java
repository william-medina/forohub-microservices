package com.williammedina.topic_read_service.infrastructure.event.model.topicfollow;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Evento emitido por el servicio de tópicos cuando un usuario sigue o deja de seguir un tópico")
public record TopicFollowEvent(
        @Schema(description = "ID único del evento", example = "123e4567-e89b-12d3-a456-426614174000")
        String eventId,

        @Schema(description = "Tipo de evento (FOLLOW, UNFOLLOW)")
        TopicFollowEventType eventType,

        @Schema(description = "Nombre del servicio que emitió el evento", example = "topic-follow-service")
        String sourceService,

        @Schema(description = "Payload del evento, contiene la información específica del cambio")
        JsonNode payload
){
    public TopicFollowEvent(TopicFollowEventType eventType, String sourceService, JsonNode payload) {
        this(UUID.randomUUID().toString(), eventType, sourceService, payload);
    }
}
