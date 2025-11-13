package com.williammedina.token_gateway.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Respuesta simplificada con la información pública del token de acceso. El refresh token se devuelve como cookie HTTP-only.")
public record AccessTokenResponseDTO(
        @Schema(description = "Token de acceso (JWT) que el cliente debe usar en las peticiones al API Gateway.", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String access_token,

        @Schema(description = "Tipo del token, normalmente 'Bearer'.", example = "Bearer")
        String token_type,

        @Schema(description = "Tiempo de expiración del access token en segundos.", example = "3600")
        Long expires_in,

        @Schema(description = "Ámbitos o permisos concedidos al token.", example = "read write")
        String scope
) {

    public static AccessTokenResponseDTO fromAuthServerResponse(TokenResponseDTO response) {
        return AccessTokenResponseDTO.builder()
                .access_token(response.access_token())
                .token_type(response.token_type())
                .expires_in(response.expires_in())
                .scope(response.scope())
                .build();
    }
}
