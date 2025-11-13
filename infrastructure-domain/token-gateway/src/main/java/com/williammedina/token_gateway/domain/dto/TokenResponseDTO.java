package com.williammedina.token_gateway.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Respuesta devuelta por el Auth Server al intercambiar un authorization code o refresh token. Contiene los tokens de acceso y actualización.")
public record TokenResponseDTO(

        @Schema(description = "Token de acceso (JWT) que debe incluirse en las peticiones protegidas.", example = "eyJraWQiOiJhYmNkZWYiLCJhbGciOiJSUzI1NiJ9...")
        String access_token,

        @Schema(description = "Token de actualización usado para obtener nuevos access tokens.", example = "def50200a2c4e3b4f7a8e3b1...")
        String refresh_token,

        @Schema(description = "Tipo de token emitido, normalmente 'Bearer'.", example = "Bearer")
        String token_type,

        @Schema(description = "Tiempo en segundos antes de que el access token expire.", example = "900")
        Long expires_in,

        @Schema(description = "Ámbitos de acceso otorgados por el servidor (scopes).", example = "read write")
        String scope
) {
}
