package com.williammedina.topic_service.domain.topic.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO que contiene el número de tópicos creados por un usuario")
public record UserTopicCountDTO(

        @Schema(description = "Número de tópicos creados por el usuario", example = "12")
        Long count

) {
}
