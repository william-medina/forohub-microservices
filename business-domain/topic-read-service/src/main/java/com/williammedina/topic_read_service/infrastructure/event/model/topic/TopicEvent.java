package com.williammedina.topic_read_service.infrastructure.event.model.topic;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Evento emitido por el servicio de tópicos cuando ocurre un cambio en un tópico")
public record TopicEvent (
        @Schema(description = "ID único del evento", example = "123e4567-e89b-12d3-a456-426614174000")
        String eventId,

        @Schema(description = "Tipo de evento (CREATED, UPDATED, DELETED, STATUS_CHANGED)")
        TopicEventType eventType,

        @Schema(description = "Nombre del servicio que emitió el evento", example = "response-service")
        String sourceService,

        @Schema(description = "Payload del evento, contiene la información específica del cambio")
        JsonNode payload
){
}
