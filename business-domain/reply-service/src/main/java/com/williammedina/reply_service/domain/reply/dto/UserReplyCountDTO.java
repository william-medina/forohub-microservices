package com.williammedina.reply_service.domain.reply.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO que contiene el número de respuestas creadas por un usuario")
public record UserReplyCountDTO(

        @Schema(description = "Número de respuestas creadas por el usuario", example = "15")
        Long count

) {}
