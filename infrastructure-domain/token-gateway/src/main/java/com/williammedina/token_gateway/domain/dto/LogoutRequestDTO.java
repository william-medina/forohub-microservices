package com.williammedina.token_gateway.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Solicitud para revocar (invalidar) un access y refresh token en el Auth Server.")
public record LogoutRequestDTO(

        @Schema(description = "Refresh token a revocar.", example = "def50200a2c4e3b4f7a8e3b1...")
        String token,

        @Schema(description = "Identificador del cliente registrado en el Authorization Server.", example = "frontend-client")
        String client_id,

        @Schema(description = "Secreto del cliente registrado.", example = "my-secret-123")
        String client_secret
) {
}
