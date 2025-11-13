package com.williammedina.email_service.infrastructure.event.model.reply;

import com.williammedina.email_service.domain.email.dto.ReplyDTO;
import com.williammedina.email_service.domain.email.dto.TopicSummaryDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Evento emitido cuando ocurre un cambio en una respuesta de un tópico (creación, actualización, eliminación o cambio de estado)")
public record ReplyPayload(

        @Schema(description = "Respuesta afectada con su información actualizada")
        ReplyDTO reply,

        @Schema(description = "Resumen del tópico donde pertenece la respuesta")
        TopicSummaryDTO topic,

        @Schema(description = "Nombre del curso al que pertenece el tópico", example = "Introducción a Java y Programación Orientada a Objetos")
        String courseName,

        @Schema(description = "ID del usuario que provocó el evento")
        Long userId

) {

}
