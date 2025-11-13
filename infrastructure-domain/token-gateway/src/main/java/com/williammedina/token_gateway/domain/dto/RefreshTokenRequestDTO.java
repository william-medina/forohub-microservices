package com.williammedina.token_gateway.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Solicitud para obtener un nuevo access token usando un refresh token previamente emitido.")
public record RefreshTokenRequestDTO(

        @Schema(description = "Tipo de grant utilizado. Debe ser 'refresh_token'.", example = "refresh_token")
        String grant_type,

        @Schema(description = "Refresh token emitido por el servidor durante la autenticación previa.", example = "def50200a2c4e3b4f7a8e3b1...")
        String refresh_token,

        @Schema(description = "Identificador del cliente registrado en el Authorization Server.", example = "frontend-client")
        String client_id,

        @Schema(description = "Secreto del cliente configurado en el Authorization Server.", example = "my-secret-123")
        String client_secret
) {
}
