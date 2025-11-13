package com.williammedina.topic_read_service.domain.topicread.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Datos resumidos del tópico")
public record TopicSummaryDTO(

        @Schema(description = "ID del tópico", example = "12")
        Long id,

        @Schema(description = "ID del curso asociado al tópico", example = "4")
        Long courseId,

        @Schema(description = "Título del tópico", example = "Error al ejecutar aplicación Spring Boot")
        String title,

        @Schema(description = "Descripción del tópico", example = "Estoy intentando ejecutar mi aplicación, pero falla al iniciar sin mostrar un mensaje claro.")
        String description,

        @Schema(description = "Datos del autor del tópico")
        UserDTO author,

        @Schema(description = "Estado actual del tópico", example = "CLOSED")
        Status status,

        @Schema(description = "Lista de ids de los seguidores del tópico")
        List<Long> followersIds,

        @Schema(description = "Fecha de creación del tópico", example = "2025-05-31T15:45:00")
        LocalDateTime createdAt,

        @Schema(description = "Fecha de última actualización del tópico", example = "2025-07-01T10:15:00")
        LocalDateTime updateAt
) {
    public enum Status {
        ACTIVE,
        CLOSED,
    }
}
