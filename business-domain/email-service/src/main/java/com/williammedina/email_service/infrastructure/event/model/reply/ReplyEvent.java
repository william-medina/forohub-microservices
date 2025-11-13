package com.williammedina.email_service.infrastructure.event.model.reply;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Evento emitido por el servicio de respuestas cuando ocurre un cambio en una respuesta")
public record ReplyEvent(
        @Schema(description = "ID único del evento", example = "123e4567-e89b-12d3-a456-426614174000")
        String eventId,

        @Schema(description = "Tipo de evento (CREATED, UPDATED, DELETED, SOLUTION_CHANGED)")
        ReplyEventType eventType,

        @Schema(description = "Nombre del servicio que emitió el evento", example = "response-service")
        String sourceService,

        @Schema(description = "Payload del evento, contiene la información específica del cambio")
        JsonNode payload
){
}
