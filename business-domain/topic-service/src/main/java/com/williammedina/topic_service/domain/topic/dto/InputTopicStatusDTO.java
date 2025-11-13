package com.williammedina.topic_service.domain.topic.dto;

import com.williammedina.topic_service.domain.topic.entity.TopicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload para cambiar el estado de un tópico.")
public record InputTopicStatusDTO(
        @Schema(description = "Nuevo estado del tópico. Solo se permiten los valores ACTIVE o CLOSED.", example = "ACTIVE", allowableValues = {"ACTIVE", "CLOSED"})
        @NotNull(message = "El estado es requerido.")
        TopicEntity.Status status,

        @Schema(description = "Identificador del usuario que realiza el cambio de estado", example = "5")
        @NotNull(message = "El id del usuario es requerido.")
        Long userId
) {
}
