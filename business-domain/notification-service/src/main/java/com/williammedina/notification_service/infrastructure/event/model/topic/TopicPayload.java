package com.williammedina.notification_service.infrastructure.event.model.topic;

import com.williammedina.notification_service.domain.notification.dto.TopicDetailsDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Evento emitido cuando ocurre un cambio en un tópico (creación, actualización, eliminación o cambio de estatus)")
public record TopicPayload(

        @Schema(description = "Tópico afectado con su información actualizada")
        TopicDetailsDTO topic,

        @Schema(description = "ID del usuario que provocó el evento")
        Long userId

) {

}
