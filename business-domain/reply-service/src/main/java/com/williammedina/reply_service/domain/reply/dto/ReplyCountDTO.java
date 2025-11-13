package com.williammedina.reply_service.domain.reply.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cantidad de respuestas asociadas a un tópico")
public record ReplyCountDTO(

        @Schema(description = "ID del tópico", example = "12")
        Long topicId,

        @Schema(description = "Cantidad de respuestas del tópico", example = "5")
        Long count
) { }
