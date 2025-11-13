package com.williammedina.token_gateway.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Solicitud enviada al Auth Server para intercambiar un authorization code por tokens de acceso y actualización.")
public record TokenRequestDTO (

        @Schema(description = "Tipo de grant que se está solicitando. Debe ser 'authorization_code'.", example = "authorization_code")
        String grant_type,

        @Schema(description = "Código temporal obtenido tras el inicio de sesión exitoso del usuario (authorization code).", example = "4f9f8e2b-7dcd-4b41-a6b0-55e12e0f4a7a")
        String code,

        @Schema(description = "URI registrada donde se redirige el flujo OAuth2 después del login.", example = "https://app.com/callback")
        String redirect_uri
) {
}
