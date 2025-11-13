package com.williammedina.topic_read_service.infrastructure.event.model.user;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Evento emitido por el servicio de usuarios cuando ocurre un cambio en los datos")
public record UserEvent(
        @Schema(description = "ID único del evento", example = "123e4567-e89b-12d3-a456-426614174000")
        String eventId,

        @Schema(description = "Tipo de evento (CREATED_ACCOUNT, RESET_PASSWORD, REQUEST_CONFIRMATION_CODE, UPDATED_USER)")
        UserEventType eventType,

        @Schema(description = "Nombre del servicio que emitió el evento", example = "user-service")
        String sourceService,

        @Schema(description = "Payload del evento, contiene la información específica del cambio")
        JsonNode payload

){
    public UserEvent(UserEventType eventType, String sourceService, JsonNode payload) {
        this(UUID.randomUUID().toString(), eventType, sourceService, payload);
    }
}
