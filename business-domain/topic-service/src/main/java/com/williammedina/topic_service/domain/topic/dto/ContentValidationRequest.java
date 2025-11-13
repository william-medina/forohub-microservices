package com.williammedina.topic_service.domain.topic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud para la validación de contenido mediante IA.")
public record ContentValidationRequest(

        @Schema(description = "Texto que se desea validar. Puede ser un título, descripción o nombre de usuario.", example = "Este es un ejemplo de contenido a validar")
        @NotBlank(message = "El texto no puede estar vacío")
        String text

) {}
