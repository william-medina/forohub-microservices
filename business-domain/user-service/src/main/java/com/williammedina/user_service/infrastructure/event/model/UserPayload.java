package com.williammedina.user_service.infrastructure.event.model;

import com.williammedina.user_service.domain.user.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Evento emitido cuando ocurre un cambio en los datos del usuario")
public record UserPayload(

        @Schema(description = "Usuario afectado con su información actualizada")
        UserDTO user,

        @Schema(description = "Email del usuario puede ser nulo", example = "user@example.com")
        String email,

        @Schema(description = "Token de confirmación de cuenta o restablecer password puede ser nulo", example = "123e4567-e89b-12d3-a456-426614174000")
        String token

) {

}
