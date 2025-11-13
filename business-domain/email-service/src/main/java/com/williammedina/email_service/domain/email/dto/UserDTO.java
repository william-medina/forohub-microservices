package com.williammedina.email_service.domain.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Información pública del usuario")
public record UserDTO(

        @Schema(description = "ID del usuario", example = "1")
        Long id,

        @Schema(description = "Nombre de usuario", example = "admin123")
        String username,

        @Schema(description = "Rol del usuario", example = "ADMIN")
        String profile
) {
}
